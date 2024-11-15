package br.mackenzie.webapp.Imovel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.mackenzie.webapp.security.dao.UserDao;
import br.mackenzie.webapp.security.model.DAOUser;
import br.mackenzie.webapp.security.model.UserDTO;

@RestController
@RequestMapping("/api/imoveis")
class ImovelController {

    @Autowired
    private ImovelRepo imovelRepo;

    public ImovelController() {
    }

    @GetMapping
    Iterable<Imovel> getImoveis(@RequestParam Optional<String> search) {
        if (search.isPresent()) {
            return imovelRepo.findByRuaContainingIgnoreCase(search.get());
        } else {
            return imovelRepo.findAll();
        }
    }

    @GetMapping("/{id}")
    Optional<Imovel> getImovel(@PathVariable long id) {
        return imovelRepo.findById(id);
    }

    @Autowired
    private UserDao userDao; // Repositório de usuários

    @PostMapping
    Imovel createImovel(@RequestBody Imovel imovel) {
        String usernameLogado = imovel.getUsuario().getUsername();
        DAOUser usuario = userDao.findByUsername(usernameLogado);
        imovel.setUsuario(usuario);
        Imovel createdImovel = imovelRepo.save(imovel);
        return createdImovel;
    }

    @PutMapping("/{imovelId}")
Imovel updateImovel(@RequestBody Imovel imovelRequest, @PathVariable long imovelId) {
    // Busca o imóvel existente pelo ID
    Optional<Imovel> existingImovel = imovelRepo.findById(imovelId);
    if (existingImovel.isPresent()) {
        Imovel imovel = existingImovel.get();

        // Atualiza os dados do imóvel existente com os dados do request
        imovel.setTitulo(imovelRequest.getTitulo());
        imovel.setDescricao(imovelRequest.getDescricao());
        imovel.setPreco(imovelRequest.getPreco());
        imovel.setFoto(imovelRequest.getFoto());
        imovel.setTipo(imovelRequest.getTipo());
        imovel.setRua(imovelRequest.getRua());
        imovel.setNumero(imovelRequest.getNumero());
        imovel.setCidade(imovelRequest.getCidade());
        imovel.setEstado(imovelRequest.getEstado());
        imovel.setCep(imovelRequest.getCep());

        // Valida e associa o usuário, se necessário
        if (imovelRequest.getUsuario() != null && imovelRequest.getUsuario().getUsername() != null) {
            DAOUser usuario = userDao.findByUsername(imovelRequest.getUsuario().getUsername());
            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
            }
            imovel.setUsuario(usuario);
        }

        // Salva as alterações
        return imovelRepo.save(imovel);
    }

    // Retorna erro se o imóvel não for encontrado
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Imóvel com ID " + imovelId + " não encontrado.");
}

    @DeleteMapping("/{id}")
    void deleteImovel(@PathVariable long id) {
        imovelRepo.deleteById(id);
    }
}