package ru.isu.antlib.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.isu.antlib.model.BookDescription;
import java.io.IOException;

public class BookParser {

    public static BookDescription findByISBN(String ISBN) throws BookNotFoundException, BookTimeoutException {
        String url = SearchParser.findBookUrlByIsbn(ISBN);
        return parseFromUrl(url);
    }

    public static BookDescription parseFromUrl(String url) throws BookTimeoutException {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            return parseBookInfo(doc);
        } catch (IOException e) {
            throw new BookTimeoutException("Не удалось получить информацию о книге. Повторите попытку позже");
        }
    }

    public static BookDescription parseBookInfo(Document doc) throws BookParseException {
        try {
            BookDescription book = new BookDescription();

            // Парсинг с защитой от NullPointerException
            Element ogTitle = doc.selectFirst("meta[property=og:title]");
            String title = "";
            if (ogTitle != null) {
                title = ogTitle.attr("content")
                        .replace("Книга: ", "")
                        .replace(". Купить книгу, читать рецензии | Лабиринт", "");
            } else {
                Element h1 = doc.selectFirst("h1");
                if (h1 != null) title = h1.text();
            }

            if (title == null || title.isEmpty()) {
                throw new BookParseException("Не удалось определить название книги");
            }

            // Автор
            String author = "";
            Elements features = doc.select("div._feature_mmfyx_1");
            for (Element feature : features) {
                Element nameDiv = feature.selectFirst("div._name_mmfyx_9");
                if (nameDiv != null && "Автор".equals(nameDiv.text())) {
                    Element authorLink = feature.selectFirst("a.text-blue-800");
                    if (authorLink != null) {
                        author = authorLink.text();
                        if (author.contains(" ")) {
                            author = author.substring(author.indexOf(' ') + 1) + " " + author.substring(0, author.indexOf(' '));
                        }
                    }
                    break;
                }
            }

            // Очистка названия от автора
            for (String s : author.split(" ")) {
                title = title.replace(s, "");
            }
            title = title.replace(" - ", "");
            book.setTitle(title.trim());
            book.setAuthor(author);

            // ISBN
            Element metaIsbn = doc.selectFirst("meta[itemprop=isbn]");
            if (metaIsbn != null) {
                book.setISBN(metaIsbn.attr("content").replace("-", ""));
            }

            // Год и издательство
            for (Element feature : features) {
                Element nameDiv = feature.selectFirst("div._name_mmfyx_9");
                if (nameDiv != null) {
                    if ("Издательство".equals(nameDiv.text())) {
                        String publisherText = feature.text();
                        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{4})");
                        java.util.regex.Matcher matcher = pattern.matcher(publisherText);
                        if (matcher.find()) {
                            book.setYear(Integer.parseInt(matcher.group(1)));
                        }
                        Element publisherLink = feature.selectFirst("a.text-blue-800");
                        if (publisherLink != null) {
                            book.setPublisher(publisherLink.text());
                        }
                    } else if ("Страниц".equals(nameDiv.text())) {
                        String pagesText = feature.text();
                        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)");
                        java.util.regex.Matcher matcher = pattern.matcher(pagesText);
                        if (matcher.find()) {
                            book.setPages(Integer.parseInt(matcher.group(1)));
                        }
                    } else if ("Язык".equals(nameDiv.text())) {
                        Element languageSpan = feature.selectFirst("span");
                        if (languageSpan != null) {
                            book.setLanguage(languageSpan.text());
                        }
                    }
                }
            }

            // Обложка
            Element ogImage = doc.selectFirst("meta[property=og:image]");
            if (ogImage != null) {
                book.setCover(ogImage.attr("content"));
            } else {
                Element coverImg = doc.selectFirst("img._image_1qke2_7");
                if (coverImg != null && coverImg.hasAttr("src")) {
                    String src = coverImg.attr("src");
                    if (!src.startsWith("http")) {
                        src = "https://www.labirint.ru" + src;
                    }
                    book.setCover(src);
                }
            }

            // Описание
            Element annotationBlock = doc.selectFirst("#annotation");
            if (annotationBlock != null) {
                Element contentDiv = annotationBlock.selectFirst("._content_eijg8_12");
                if (contentDiv == null) {
                    contentDiv = annotationBlock.selectFirst("._wrapper_1rsml_1");
                }
                if (contentDiv != null) {
                    contentDiv.select(".cursor-pointer, ._list_1esc6_1, ._tabs_up3jt_1, ._button_up3jt_13").remove();
                    String annotation = contentDiv.text().replaceAll("\\s+", " ").trim();
                    book.setDescription(annotation);
                }
            }

            return book;

        } catch (Exception e) {
            throw new BookParseException("Ошибка при обработке данных книги: " + e.getMessage());
        }
    }
}