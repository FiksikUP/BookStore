package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Repository
public class BookRepository implements ProjectRepository<Book>, ApplicationContextAware {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    //    private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;
    private final NamedParameterJdbcTemplate jdbcTempalte;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate jdbcTempalte) {
        this.jdbcTempalte = jdbcTempalte;
    }

    @Override
    public List<Book> retreiveAll() {
        List<Book> books = jdbcTempalte.query("SELECT * FROM books", (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        });
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", book.getAuthor());
        parameterSource.addValue("title", book.getTitle());
        parameterSource.addValue("size", book.getSize());
        jdbcTempalte.update("INSERT INTO books(author,title,size) VALUES(:author,:title,:size)", parameterSource);
        logger.info("store new book: " + book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", bookIdToRemove);
        jdbcTempalte.update("DELETE FROM books WHERE id = :id", parameterSource);
        logger.info("remove book completed");
        return true;
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
//                statusRemove = repo.remove(book);
            }
        }
        return statusRemove;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void defaultInit() {
        logger.info("default INIT in book repo bean");
    }

    private void defaultDestroy() {
        logger.info("default DESTROY in book repo bean");
    }
}
