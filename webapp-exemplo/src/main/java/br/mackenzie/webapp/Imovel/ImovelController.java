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

    @PostMapping
    Imovel createImovel(@RequestBody Imovel imovel) {
        Imovel createdImovel = imovelRepo.save(imovel);
        return createdImovel;
    }

    @PutMapping("/{imovelId}")
    Optional<Imovel> updateImovel(@RequestBody Imovel imovelRequest, @PathVariable long imovelId) {
        Optional<Imovel> opt = imovelRepo.findById(imovelId);
        if (opt.isPresent()) {
            if (imovelRequest.getId().equals(imovelId)) {
                imovelRepo.save(imovelRequest);
                return opt;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Erro ao alterar dados do imóvel com id " + imovelId);
    }

    @DeleteMapping("/{id}")
    void deleteImovel(@PathVariable long id) {
        imovelRepo.deleteById(id);
    }
}