package pl.pla9er9.qwery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

    private final Trie trie;

    public ApplicationController(Trie trie) {
        this.trie = trie;
    }

    @GetMapping("/search")
    public String[] search(
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return trie.search(value, limit);
    }
}
