package pl.pla9er9.qwery;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ApplicationController {

    private final Trie trie;

    public ApplicationController(Trie trie) {
        this.trie = trie;
    }

    @GetMapping("records/search")
    public String[] search(
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return trie.search(value, limit);
    }

    @PostMapping("records")
    public String addRecord(@RequestParam String value) {
        trie.add(value);
        return value;
    }

    @DeleteMapping("records/v/{value}")
    public String deleteRecord(@PathVariable String value) {
        var result = trie.search(value, 1);
        var found = result.length == 1;

        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        trie.delete(value);

        return result[0];
    }

    @GetMapping("records/all")
    public String[] getAllRecords() {
        return trie.getAll();
    }

    @DeleteMapping("records/all")
    public void deleteAllRecords() {
        trie.deleteAll();
    }
}
