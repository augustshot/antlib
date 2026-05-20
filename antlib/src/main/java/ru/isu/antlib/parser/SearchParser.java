package ru.isu.antlib.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SearchParser {
    public static String findBookUrlByIsbn(String isbn) {
        // Формируем URL поиска
        String searchUrl = "https://www.labirint.ru/search/" + isbn + "%20/?stype=0";

        try {
            // Выполняем GET-запрос с user-agent (чтобы не блокировали)
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            // Ищем все ссылки на книги в результатах поиска
            // Селектор: ищем <a> внутри .product-card__img
            Elements productLinks = doc.select("a.product-card__img");

            // Фильтруем: оставляем только ссылки, ведущие на /books/{id}/
            for (Element link : productLinks) {
                String href = link.attr("href");
                if (href != null && href.startsWith("/books/") && href.endsWith("/")) {
                    // Формируем полный URL
                    String fullUrl = "https://www.labirint.ru" + href;
                    return fullUrl;
                }
            }

            // Если ничего не нашли — проверяем альтернативный селектор
            Elements alternativeLinks = doc.select("div.product-card a");
            for (Element link : alternativeLinks) {
                String href = link.attr("href");
                if (href != null && href.matches("/books/\\d+/?")) {
                    if (!href.endsWith("/")) href += "/";
                    return "https://www.labirint.ru" + href;
                }
            }

            // Проверяем, есть ли сообщение "ничего не найдено"
            String pageText = doc.text();
            if (pageText.contains("ничего не найдено") ||
                    pageText.contains("Ничего не найдено") ||
                    doc.select("h1:contains(ничего не найдено)").size() > 0) {
                System.out.println("Книга с ISBN " + isbn + " не найдена.");
                return null;
            }

            // Если результатов нет, но страница загрузилась
            if (productLinks.isEmpty()) {
                System.out.println("По запросу " + isbn + " нет результатов или ссылка не распознана.");
                return null;
            }

        } catch (IOException e) {
            System.err.println("Ошибка при запросе: " + e.getMessage());
            return null;
        }

        return null;
    }

    // Дополнительный метод: получить ID книги из URL
    public static String extractBookIdFromUrl(String bookUrl) {
        if (bookUrl == null) return null;
        // Формат: https://www.labirint.ru/books/575192/
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/books/(\\d+)/");
        java.util.regex.Matcher matcher = pattern.matcher(bookUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
