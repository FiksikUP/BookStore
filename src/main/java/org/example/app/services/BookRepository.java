package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Repository
public class BookRepository implements ProjectRepository<Book> {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private final List<Book> repo = new ArrayList<>();

    @Override
    public List<Book> retreiveAll() {
        return new ArrayList<>(repo);
    }

    @Override
    public void store(Book book) {
        book.setId(book.hashCode());
        logger.info("store new book: " + book);
        repo.add(book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        for (Book book : retreiveAll()) {
            if (book.getId().equals(bookIdToRemove)) {
                logger.info("remove book completed: " + book);
                return repo.remove(book);
            }
        }
        return false;
    }

    @Override
    public boolean removeItemByRegex(String regex) {
        boolean statusRemove = false;
        Pattern pattern = Pattern.compile(regex);
        for (Book book : retreiveAll()) {
            if (pattern.matcher(Objects.toString(book.getId(), "null")).matches() ||
                    pattern.matcher(Objects.toString(book.getAuthor(), "null")).matches() ||
                    pattern.matcher(Objects.toString(book.getTitle(), "null")).matches() ||
                    pattern.matcher(Objects.toString(book.getSize(), "null")).matches()) {
                logger.info("remove book completed: " + book);
                statusRemove = repo.remove(book);
            }
        }
        return statusRemove;
    }
}
