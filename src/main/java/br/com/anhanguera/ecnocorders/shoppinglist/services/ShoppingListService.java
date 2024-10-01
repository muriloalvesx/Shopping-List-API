package br.com.anhanguera.ecnocorders.shoppinglist.services;

import br.com.anhanguera.ecnocorders.shoppinglist.dtos.ShoppingListDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.ShoppingList;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.UsersModel; // Importar UsersModel
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

    // Método para buscar lista pelo ID e nome de usuário, agora incluindo o ID no DTO
    public ShoppingListDTO getShoppingListByIdAndUsername(Long id, String username) {
        // Busca a lista no banco de dados pelo ID e username
        Optional<ShoppingList> shoppingList = shoppingListRepository.findByIdAndUser_Login(id, username);

        // Transforma o modelo da entidade em DTO e retorna, agora incluindo o ID
        return shoppingList.map(list -> {
            List<String> itemNames = list.getItems().stream()
                    .map(ItemList::getNome)
                    .collect(Collectors.toList());
            // Inclui o ID na construção do DTO
            return new ShoppingListDTO(list.getId(), list.getNome(), itemNames, list.getUser().getLogin());
        }).orElse(null);
    }

    // Método para criar uma nova lista de compras
    public ShoppingList criarLista(ShoppingListDTO shoppingListDTO) {
        // Obtém o usuário a partir do nome de login presente no DTO
        UsersModel user = userRepository.findByLogin(shoppingListDTO.getUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setNome(shoppingListDTO.getNome()); // Define o nome da lista
        shoppingList.setUser(user); // Define o usuário dono da lista

        // Corrigido para usar getItens
        List<ItemList> items = (shoppingListDTO.getItens() != null ? shoppingListDTO.getItens() : new ArrayList<>()).stream()
                .map(itemName -> {
                    ItemList item = new ItemList();
                    item.setNome((String) itemName); // Define o nome do item
                    item.setShoppingList(shoppingList); // Define a relação com a lista
                    return item;
                })
                .collect(Collectors.toList());

        shoppingList.setItems(items); // Define os itens na lista
        return shoppingListRepository.save(shoppingList); // Salva a nova lista no banco de dados
    }

    // Método para buscar as listas de compras do usuário logado
    public List<ShoppingListDTO> getShoppingListsByUsername(String username) {
        // Encontra o usuário pelo nome de login
        UsersModel user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<ShoppingList> shoppingLists = shoppingListRepository.findByUser(user); // Passa a instância do usuário

        return shoppingLists.stream()
                .map(this::convertToDTO) // Converte cada lista para DTO
                .collect(Collectors.toList());
    }

    public void deleteShoppingListByIdAndUsername(Long id, String username) {
        // Verifica se a lista existe e pertence ao usuário
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lista de compras não encontrada ou não pertence ao usuário"));

        // Deleta a lista de compras
        shoppingListRepository.delete(shoppingList);
    }

    // Método para adicionar um item à lista de compras
    public ShoppingListDTO addItemToList(Long listId, String itemName) {
        // Verifica se a lista pertence ao usuário
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Lista de compras não encontrada ou não pertence ao usuário"));

        // Cria o novo item e o adiciona à lista
        ItemList newItem = new ItemList();
        newItem.setNome(itemName);
        newItem.setShoppingList(shoppingList);
        itemListRepository.save(newItem); // Salva o novo item

        shoppingList.getItems().add(newItem); // Adiciona o item à lista

        // Converte para DTO e retorna
        return convertToDTO(shoppingListRepository.save(shoppingList)); // Salva a lista com o item adicionado
    }

    // Método para remover um item da lista de compras
    public ShoppingListDTO removeItemFromList(Long listId, int itemIndex) {
        // Verifica se a lista pertence ao usuário
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Lista de compras não encontrada ou não pertence ao usuário"));

        List<ItemList> items = shoppingList.getItems();

        // Verifica se o índice do item é válido
        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw new RuntimeException("Índice de item inválido");
        }

        ItemList itemToRemove = items.get(itemIndex);
        items.remove(itemToRemove); // Remove o item da lista
        itemListRepository.delete(itemToRemove); // Remove o item do banco de dados

        // Salva e retorna a lista atualizada
        return convertToDTO(shoppingListRepository.save(shoppingList));
    }

    // Converte a entidade para o DTO, agora incluindo o ID
    private ShoppingListDTO convertToDTO(ShoppingList shoppingList) {
        List<String> itemNames = shoppingList.getItems().stream()
                .map(ItemList::getNome)
                .collect(Collectors.toList());

        // Inclui o ID da lista na criação do DTO
        return new ShoppingListDTO(shoppingList.getId(), shoppingList.getNome(), itemNames, shoppingList.getUser().getLogin());
    }
}