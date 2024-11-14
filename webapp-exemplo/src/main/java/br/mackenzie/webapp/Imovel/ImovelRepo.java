package br.mackenzie.webapp.Imovel;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ImovelRepo extends CrudRepository<Imovel, Long> {
    List<Imovel> findByTituloContainingIgnoreCase(String titulo);
    List<Imovel> findByRuaContainingIgnoreCase(String rua);
    List<Imovel> findByTipo(String tipo);
    List<Imovel> findByPrecoLessThan(double preco);
}