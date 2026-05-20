package ru.isu.antlib.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.isu.antlib.model.BookDescription;

import java.io.IOException;
import java.util.Arrays;

public class BookParser {
    public static BookDescription findByISBN(String ISBN){
        try {
            // Загрузка HTML файла (можно заменить на connection для URL)
            String url = SearchParser.findBookUrlByIsbn(ISBN);
            if(url == null) return null;

            return parseFromUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BookDescription parseFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get();
        return parseBookInfo(doc);
    }

    public static BookDescription parseBookInfo(Document doc) {
        BookDescription book = new BookDescription();

        // 1. Название (title)
        // Из <meta property="og:title"> или <h1>
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        String title = "";
        if (ogTitle != null) {
            title = (ogTitle.attr("content").replace("Книга: ", "").replace(". Купить книгу, читать рецензии | Лабиринт", ""));
        } else {
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) title = (h1.text());
        }

        // 2. Автор (author)
        // Из блока характеристик: <div class="_feature_mmfyx_1"> с заголовком "Автор"
        String author = "";
        Elements features = doc.select("div._feature_mmfyx_1");
        for (Element feature : features) {
            Element nameDiv = feature.selectFirst("div._name_mmfyx_9");
            if (nameDiv != null && "Автор".equals(nameDiv.text())) {
                Element authorLink = feature.selectFirst("a.text-blue-800");
                if (authorLink != null) {
                    author = authorLink.text();
                    author = author.indexOf(' ') > 0 ? author.substring(author.indexOf(' ') + 1) + " " + author.substring(0, author.indexOf(' ')) : author;
                    book.setAuthor(author);
                }
                break;
            }
        }
        for(String s : author.split(" ")){
            title = title.replace(s, "");
        }
        title = title.replace(" - ", "");
        book.setTitle(title.trim());

        // 3. ISBN
        // Из <meta itemprop="isbn">
        Element metaIsbn = doc.selectFirst("meta[itemprop=isbn]");
        if (metaIsbn != null) {
            book.setISBN(metaIsbn.attr("content").replace("-", ""));
        } else {
            // Альтернатива: из блока характеристик
            for (Element feature : features) {
                Element nameDiv = feature.selectFirst("div._name_mmfyx_9");
                if (nameDiv != null && "ISBN".equals(nameDiv.text())) {
                    Element isbnSpan = feature.selectFirst("span");
                    if (isbnSpan != null) {
                        book.setISBN(isbnSpan.text().replace("-", ""));
                    }
                    break;
                }
            }
        }

        // 4. Год издания (year)
        // Из блока характеристик: "Издательство Время, 2017"
        for (Element feature : features) {
            Element nameDiv = feature.selectFirst("div._name_mmfyx_9");
            if (nameDiv != null && "Издательство".equals(nameDiv.text())) {
                String publisherText = feature.text();
                // Извлекаем год (4 цифры)
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{4})");
                java.util.regex.Matcher matcher = pattern.matcher(publisherText);
                if (matcher.find()) {
                    book.setYear(Integer.parseInt(matcher.group(1)));
                }
                // Извлекаем издательство
                Element publisherLink = feature.selectFirst("a.text-blue-800");
                if (publisherLink != null) {
                    book.setPublisher(publisherLink.text());
                }
                break;
            }
        }

        // 5. Количество страниц (pages)
        for (Element feature : features) {
            Element nameDiv = feature.selectFirst("div._name_mmfyx_9");
            if (nameDiv != null && "Страниц".equals(nameDiv.text())) {
                String pagesText = feature.text();
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)");
                java.util.regex.Matcher matcher = pattern.matcher(pagesText);
                if (matcher.find()) {
                    book.setPages(Integer.parseInt(matcher.group(1)));
                }
                break;
            }
        }

        // 6. Язык (language)
        // Из блока характеристик: <div class="_feature_mmfyx_1"> с заголовком "Язык"
        for (Element feature : features) {
            Element nameDiv = feature.selectFirst("div._name_mmfyx_9");
            if (nameDiv != null && "Язык".equals(nameDiv.text())) {
                Element languageSpan = feature.selectFirst("span");
                if (languageSpan != null) {
                    book.setLanguage(languageSpan.text());
                }
                break;
            }
        }

        // 7. Издательство (publisher) - уже извлечено выше в блоке года

        // 8. Обложка (cover)
        // Из <meta property="og:image">
        Element ogImage = doc.selectFirst("meta[property=og:image]");
        if (ogImage != null) {
            book.setCover(ogImage.attr("content"));
        } else {
            // Альтернатива: из изображения в галерее
            Element coverImg = doc.selectFirst("img._image_1qke2_7");
            if (coverImg != null && coverImg.hasAttr("src")) {
                String src = coverImg.attr("src");
                if (!src.startsWith("http")) {
                    src = "https://www.labirint.ru" + src;
                }
                book.setCover(src);
            }
        }

        // 9. Описание (description)
        // Из <meta name="description">
        Element annotationBlock = doc.selectFirst("#annotation");

        if (annotationBlock != null) {
            // Ищем div с классом _content_eijg8_12 или _wrapper_1rsml_1
            Element contentDiv = annotationBlock.selectFirst("._content_eijg8_12");
            if (contentDiv == null) {
                contentDiv = annotationBlock.selectFirst("._wrapper_1rsml_1");
            }

            if (contentDiv != null) {
                // Удаляем кнопку "Свернуть" и стрелку, если они есть
                contentDiv.select(".cursor-pointer, ._list_1esc6_1").remove();
                contentDiv.select("._tabs_up3jt_1").remove();
                contentDiv.select("._button_up3jt_13").remove();

                String annotation = contentDiv.text();

                // Очищаем от лишних пробелов и переносов
                annotation = annotation.replaceAll("\\s+", " ").trim();

                // Если аннотация слишком длинная, можно обрезать, но лучше оставить полной
                book.setDescription(annotation);
            }
        }

        return book;
    }


    }