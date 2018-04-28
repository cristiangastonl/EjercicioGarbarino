package com.garbarino.productos.service.test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.garbarino.productos.entity.Brand;
import com.garbarino.productos.entity.Product;
import com.garbarino.productos.service.impl.ProductServiceImpl;

/**
 * @author Gaston
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {
       
    @Autowired
    private ProductServiceImpl productService;
 
    
    /**
     * 	Test que comprueba el correcto funcionamiento del agregado de un producto a la tabla de productos.
     * 
     * @throws Exception
     */
    @Test
    public void TestAddNewProduct() throws Exception {    	
    	Product cubierto = new Product("Cubiertos","Cubiertos",200,Brand.COMPUMUNDO);   	    	
    	List<Product> lista = new ArrayList<>();
    	lista.add(productService.addNewProduct(cubierto).getBody().getResults());
    	assertThat(productService.findAllByCondition(null, null).getBody().getResults(), equalTo(lista)); 
    	productService.getRepository().deleteAll();
    }
    
    /**
     * 	Test que evualua el correcto funcionamiento de la actualizacion del stock.
     * 
     * @throws Exception
     */
    @Test
    public void TestStock() throws Exception {
    	Product cubierto = new Product("Cubiertos","Tenedor",200,Brand.COMPUMUNDO); 
    	Product compu = new Product("Computadora","i7",200,Brand.GARBARINO); 
    	Integer idCubierto = productService.addNewProduct(cubierto).getBody().getResults().getId();
    	Integer idCompu = productService.addNewProduct(compu).getBody().getResults().getId();
    	
    	assertThat(productService.updateStock(idCubierto, 10).getBody().toString(), equalTo(ProductServiceImpl.STOCK_MODIFICADO_CORRECTAMENTE));
    	
    	assertThat(productService.updateStock(idCubierto, 10).getBody().toString(), equalTo(ProductServiceImpl.STOCK_MODIFICADO_CORRECTAMENTE));
    	assertThat(productService.get(idCubierto).getStock(), equalTo(20));
    	
    	assertThat(productService.updateStock(idCompu, -10).getBody().toString(), equalTo(ProductServiceImpl.ERROR_AL_MODIFICAR_EL_STOCK));
    	assertThat(productService.get(idCompu).getStock(), equalTo(0));
    	
    	assertThat(productService.updateStock(idCubierto, -10).getBody().toString(), equalTo(ProductServiceImpl.STOCK_MODIFICADO_CORRECTAMENTE));
    	assertThat(productService.get(idCubierto).getStock(), equalTo(10));
    	
    	assertThat(productService.updateStock(idCubierto, 10).getBody().toString(), equalTo(ProductServiceImpl.STOCK_MODIFICADO_CORRECTAMENTE));
    	assertThat(productService.get(idCubierto).getStock(), equalTo(20));
    	
    	assertThat(productService.updateStock(idCubierto, -10).getBody().toString(), equalTo(ProductServiceImpl.STOCK_MODIFICADO_CORRECTAMENTE));
    	assertThat(productService.get(idCubierto).getStock(), equalTo(10));
    	
    	assertThat(productService.updateStock(idCompu, 10).getBody().toString(), equalTo(ProductServiceImpl.STOCK_MODIFICADO_CORRECTAMENTE));
    	assertThat(productService.get(idCompu).getStock(), equalTo(10));
    	
    	productService.getRepository().deleteAll();
    	
    }
    
    /**
     *  Este test evualua el funcionamiento del servicio que lista todos los productos y/o los filtra por
     *  Brand y si tienen o no stock.
     * 
     * @throws Exception
     */
    @Test
    public void TestFindAllByCondition() throws Exception {
    	
    	Product cubierto = new Product("Cubiertos","Cubiertos",200,Brand.COMPUMUNDO);   	    	
    	List<Product> lista = new ArrayList<>();
    	List<Product> listaVacia = new ArrayList<>();
    	//compruebo que la lista este vacia
    	assertThat(productService.findAllByCondition(null, null).getBody().getResults(), equalTo(lista)); 
    	//agrego un nuevo producto
    	cubierto = productService.addNewProduct(cubierto).getBody().getResults();
    	lista.add(cubierto);
    	// si filtro por brand=COMPUMUNDO y active=false, entonces tengo que filtra por el que acabo de agregar.
    	assertThat(productService.findAllByCondition(Brand.COMPUMUNDO, false).getBody().getResults(), equalTo(lista)); 
    	// pero en este filtro no deberia aparecer.
    	assertThat(productService.findAllByCondition(Brand.COMPUMUNDO, true).getBody().getResults(), equalTo(listaVacia));
    	
    	assertThat(productService.findAllByCondition(Brand.GARBARINO, true).getBody().getResults(), equalTo(listaVacia));
    	assertThat(productService.findAllByCondition(Brand.GARBARINO, false).getBody().getResults(), equalTo(listaVacia));
    	
    	productService.updateStock(cubierto.getId(), 10);
    	// agrego 10 unidades de cubierto.
    	assertThat(productService.findAllByCondition(Brand.GARBARINO, false).getBody().getResults(), equalTo(listaVacia));
    	assertThat(productService.findAllByCondition(Brand.GARBARINO, true).getBody().getResults(), equalTo(listaVacia));
    	// en la unica lista que va a estar ahora, es cuando haga brand=COMPUMUNDO y active=true, ya que le asigne una unidad.
    	assertThat(productService.findAllByCondition(Brand.COMPUMUNDO, true).getBody().getResults(), equalTo(lista));
    	assertThat(productService.findAllByCondition(Brand.COMPUMUNDO, false).getBody().getResults(), equalTo(listaVacia)); 
    	// en esta lista tambien deberia estar, ya que si no envio un brand, y envio un actrive=true, el que agregue cumple
    	assertThat(productService.findAllByCondition(null, true).getBody().getResults(), equalTo(lista));
    	assertThat(productService.findAllByCondition(null, false).getBody().getResults(), equalTo(listaVacia));
    	
    	//finalmente si no le paso ningun parametro, la lista resultante ya no es vacia, ya que todos los elementos es el unico que agregue.
    	assertThat(productService.findAllByCondition(null, null).getBody().getResults(), equalTo(lista));
    	
    	productService.getRepository().deleteAll();
    	
    }
}