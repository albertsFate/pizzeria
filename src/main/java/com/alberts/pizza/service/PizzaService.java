package com.alberts.pizza.service;

import com.alberts.pizza.persistence.repository.PizzaPagSortRepository;
import com.alberts.pizza.persistence.repository.PizzaRepository;
import com.alberts.pizza.persistence.entity.PizzaEntity;
import com.alberts.pizza.service.dto.UpdatePizzaPriceDto;
import com.alberts.pizza.service.exception.EmailApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PizzaService {

   private final PizzaRepository pizzaRepository;
   private final PizzaPagSortRepository pizzaPagSortRepository;

    @Autowired
    public PizzaService(PizzaRepository pizzaRepository, PizzaPagSortRepository pizzaPagSortRepository) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaPagSortRepository = pizzaPagSortRepository;
    }

    public Page<PizzaEntity> getall(int page, int elements){
        Pageable pageRequest = PageRequest.of(page, elements);
        return this.pizzaPagSortRepository.findAll(pageRequest);
    }
    //consulta pizzas por su nombre
    public PizzaEntity getByName(String name){
        return this.pizzaRepository.findFirstByAvailableTrueAndNameIgnoreCase(name).orElseThrow(()-> new RuntimeException("La pizza no existe."));
    }
    //consulta pizzas por sus ingredientes
    public List<PizzaEntity> getWith(String ingredient){
        return this.pizzaRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCase(ingredient);
    }
    //consulta pizzas por su descripción
    public List<PizzaEntity> getWithout(String ingredient){
        return this.pizzaRepository.findAllByAvailableTrueAndDescriptionNotContainingIgnoreCase(ingredient);
    }
    //obtiene  las pizzas con valor menos precio
    public List<PizzaEntity> getCheapest(Double price){
        return this.pizzaRepository.findTop3ByAvailableTrueAndPriceLessThanEqualOrderByPriceAsc(price);
    }
    //consulta todas las pizzas disponibles
    public Page<PizzaEntity> getAvailable(int page, int elements, String sortBy, String sortDirection){
        System.out.println(this.pizzaRepository.countByVeganTrue());

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageRequest = PageRequest.of(page, elements, sort);

        return this.pizzaPagSortRepository.findByAvailableTrue(pageRequest);
    }
    //consulta una pizza
    public PizzaEntity get(int idPizza){
        return this.pizzaRepository.findById(idPizza).orElse(null
        );
    }
    //guarda una pizza
    public PizzaEntity save(PizzaEntity pizza){
        return  this.pizzaRepository.save(pizza);
    }
    //verifica si existe una pizza
    public boolean exists(int idPizza){
            return this.pizzaRepository.existsById(idPizza);
    }
    //borra una pizza
    public void delete(int idPizza){
        this.pizzaRepository.deleteById(idPizza);
    }

    @Transactional(noRollbackFor = EmailApiException.class)
    public void updatePrice(UpdatePizzaPriceDto dto){
        this.pizzaRepository.updatePrice(dto);
        this.sendEmail();
    }
    private void  sendEmail(){
    throw new EmailApiException();
    }
}
