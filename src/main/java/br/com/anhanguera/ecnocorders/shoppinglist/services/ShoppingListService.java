package br.com.anhanguera.ecnocorders.shoppinglist.services;

import br.com.anhanguera.ecnocorders.shoppinglist.dtos.ShoppingListDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.ShoppingList;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.UsersModel;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.ItemList;
import br.com.anhanguera.ecnocorders.shoppinglist.repository.ItemRepository;
import br.com.anhanguera.ecnocorders.shoppinglist.repository.ShoppingListRepository;
import br.com.anhanguera.ecnocorders.shoppinglist.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemListRepository;

    public ShoppingListService(ShoppingListRepository shoppingListRepository, UserRepository userRepository, ItemRepository itemListRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.userRepository = userRepository;
        this.itemListRepository = itemListRepository;
    }

    // Método para buscar lista pelo ID e nome de usuário
    public ShoppingListDTO getShoppingListByIdAndUsername(Long id, String username) {
        Optional<ShoppingList> shoppingList = shoppingListRepository.findByIdAndUser_Login(id, username);

        return shoppingList.map(list -> {
            List<String> itemNames = list.getItems().stream()
                    .map(ItemList::getNome)
                    .collect(Collectors.toList());
            return new ShoppingListDTO(list.getId(), list.getNome(), itemNames, list.getUser().getLogin());
        }).orElse(null);
    }

    // Método para criar uma nova lista de compras
    public ShoppingList criarLista(ShoppingListDTO shoppingListDTO) {
        UsersModel user = userRepository.findByLogin(shoppingListDTO.getUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setNome(shoppingListDTO.getNome());
        shoppingList.setUser(user);

        List<ItemList> items = (shoppingListDTO.getItens() != null ? shoppingListDTO.getItens() : new ArrayList<>()).stream()
                .map(itemName -> {
                    ItemList item = new ItemList();
                    item.setNome((String) itemName);
                    item.setShoppingList(shoppingList);
                    return item;
                })
                .collect(Collectors.toList());

        shoppingList.setItems(items);
        return shoppingListRepository.save(shoppingList);
    }

    // Método para buscar as listas de compras do usuário logado
    public List<ShoppingListDTO> getShoppingListsByUsername(String username) {
        UsersModel user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<ShoppingList> shoppingLists = shoppingListRepository.findByUser(user);

        return shoppingLists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteShoppingListByIdAndUsername(Long id, String username) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lista de compras não encontrada ou não pertence ao usuário"));

        shoppingListRepository.delete(shoppingList);
    }

    // Método para adicionar um item à lista de compras
    public ShoppingListDTO addItemToList(Long listId, String itemName) {
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Lista de compras não encontrada ou não pertence ao usuário"));

        ItemList newItem = new ItemList();
        newItem.setNome(itemName);
        newItem.setShoppingList(shoppingList);
        itemListRepository.save(newItem);

        shoppingList.getItems().add(newItem);

        return convertToDTO(shoppingListRepository.save(shoppingList));
    }

    // Método para remover um item da listA
    public ShoppingListDTO removeItemFromList(Long listId, int itemIndex) {
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Lista de compras não encontrada ou não pertence ao usuário"));

        List<ItemList> items = shoppingList.getItems();

        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw new RuntimeException("Índice de item inválido");
        }

        ItemList itemToRemove = items.get(itemIndex);
        items.remove(itemToRemove);
        itemListRepository.delete(itemToRemove);

        return convertToDTO(shoppingListRepository.save(shoppingList));
    }

    // Método para converter a entidade para o DTO
    private ShoppingListDTO convertToDTO(ShoppingList shoppingList) {
        List<String> itemNames = shoppingList.getItems().stream()
                .map(ItemList::getNome)
                .collect(Collectors.toList());

        return new ShoppingListDTO(shoppingList.getId(), shoppingList.getNome(), itemNames, shoppingList.getUser().getLogin());
    }
}
