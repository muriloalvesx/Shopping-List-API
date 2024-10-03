package br.com.anhanguera.ecnocorders.shoppinglist.controllers;

import br.com.anhanguera.ecnocorders.shoppinglist.dtos.ShoppingListDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.ShoppingList;
import br.com.anhanguera.ecnocorders.shoppinglist.services.ShoppingListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    // Requisição POST para criar uma nova lista de compras
    @PostMapping("/shopping")
    public ResponseEntity<ShoppingList> criarShoppingList(@RequestBody ShoppingListDTO shoppingListDTO) {
        ShoppingList novaLista = shoppingListService.criarLista(shoppingListDTO);
        return ResponseEntity.ok(novaLista);
    }

    // Requisição GET para buscar as listas de compras do usuário logado
    @GetMapping("/shopping")
    public ResponseEntity<List<ShoppingListDTO>> getShoppingListsByUsername(@RequestParam String username) {
        List<ShoppingListDTO> shoppingLists = shoppingListService.getShoppingListsByUsername(username);
        return ResponseEntity.ok(shoppingLists);
    }

    // Requisição GET para buscar lista especifica ao visualizar
    @GetMapping("/shopping/{id}")
    public ResponseEntity<ShoppingListDTO> getShoppingList(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        ShoppingListDTO shoppingList = shoppingListService.getShoppingListByIdAndUsername(id, username);

        if (shoppingList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(shoppingList);
    }

    // Requisição DELETE para deletar uma lista
    @DeleteMapping("/shopping/{id}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        shoppingListService.deleteShoppingListByIdAndUsername(id, username);


        return ResponseEntity.noContent().build();
    }

    // Requisição POST para adicionar item em uma lista existente
    @PostMapping("/shopping/{listId}/items")
    public ResponseEntity<ShoppingListDTO> addItem(@PathVariable Long listId, @RequestBody Map<String, String> requestBody) {
        String itemName = requestBody.get("nome");
        if (itemName == null || itemName.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }

        ShoppingListDTO updatedList = shoppingListService.addItemToList(listId, itemName);
        return ResponseEntity.ok(updatedList);
    }

    // Requisição DELETE para deletar item de uma lista existente
    @DeleteMapping("/shopping/{listId}/items/{itemIndex}")
    public ResponseEntity<ShoppingListDTO> removeItem(@PathVariable Long listId, @PathVariable int itemIndex) {
        ShoppingListDTO updatedList = shoppingListService.removeItemFromList(listId, itemIndex);
        return ResponseEntity.ok(updatedList);
    }
}
