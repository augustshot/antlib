package ru.isu.antlib.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.isu.antlib.exception.BookNotFoundException;
import ru.isu.antlib.exception.BookTimeoutException;

import java.io.IOException;

public class SearchParser {

    public static String findBookUrlByIsbn(String isbn) throws BookNotFoundException, BookTimeoutException {
        String searchUrl = "https://www.labirint.ru/search/" + isbn + "%20/?stype=0";

        try {
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            // Ищем ссылку на книгу
            Elements productLinks = doc.select("a.product-card__img");

            for (Element link : productLinks) {
                String href = link.attr("href");
                if (href != null && href.startsWith("/books/") && href.endsWith("/")) {
                    return "https://www.labirint.ru" + href;
                }
            }

            // Альтернативный поиск
            Elements alternativeLinks = doc.select("div.product-card a");
            for (Element link : alternativeLinks) {
                String href = link.attr("href");
                if (href != null && href.matches("/books/\\d+/?")) {
                    if (!href.endsWith("/")) href += "/";
                    return "https://www.labirint.ru" + href;
                }
            }

            // Книга не найдена - кидаем исключение
            throw new BookNotFoundException("Книга не найдена. Проверьте корректность ISBN или введите данные вручную");

        } catch (IOException e) {
            throw new BookTimeoutException("Не удалось получить информацию о книге. Повторите попытку позже");
        }
    }
}