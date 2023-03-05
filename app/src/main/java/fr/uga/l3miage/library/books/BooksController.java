package fr.uga.l3miage.library.books;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.library.authors.AuthorDTO;
import fr.uga.l3miage.library.authors.AuthorsController;
import fr.uga.l3miage.library.service.BookService;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.DeleteAuthorException;
import fr.uga.l3miage.library.service.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class BooksController {

    private final BookService bookService;
    private final BooksMapper booksMapper;

    private final AuthorService authorService;

    @Autowired
    public BooksController(BookService bookService, BooksMapper booksMapper, AuthorService authorService) {
       this.bookService = bookService;
       this.booksMapper = booksMapper;
       this.authorService = authorService;
    }

    @GetMapping("/books/v1")
    public Collection<BookDTO> books(@RequestParam("q") String query) {
        return null;
    }

    @GetMapping("/books/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO book(@PathVariable("id") Long id) {

        Book livre = null;
        try {
            livre = this.bookService.get(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return booksMapper.entityToDTO(livre);


    }

    @PostMapping("/authors/{authorId}/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO newBook(@PathVariable("authorId") Long authorId, @RequestBody BookDTO book) {
        try {
            authorService.get(authorId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        try {
            // On vérifie que le livre créé a des parametres valides
            if(book.title().replaceAll("\\s", "").equals("") || Long.toString(book.year()).length() > 4 || Long.toString(book.isbn()).length() < 10){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            var livre = this.booksMapper.dtoToEntity(book);
            this.bookService.save(authorId, livre);
            return this.booksMapper.entityToDTO(livre);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }


    public BookDTO updateBook(Long authorId, BookDTO book) {
        // attention BookDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        return null;
    }

    @DeleteMapping("/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) throws EntityNotFoundException {
        var livre = this.bookService.get(id);
        if(livre==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        this.bookService.delete(id);
    }



    public void addAuthor(Long authorId, AuthorDTO author) {

    }
}
