package br.com.sicredi.produto.api;

import br.com.sicredi.produto.business.ProductBusiness;
import br.com.sicredi.produto.domain.Product;
import br.com.sicredi.produto.infra.ProductTransport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class ProductsAPI {

    private ProductBusiness productBusiness;

    public ProductsAPI(final ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    @RequestMapping(value = "/products/", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST

    )
    public ResponseEntity<Product> createProduct(@Valid @RequestBody final ProductTransport productTransport) {

        return ResponseEntity.ok(productBusiness.createProduct(productTransport.getName(), productTransport.getPrice()));
    }

    @RequestMapping(value = "/products/{id}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET

    )
    public ResponseEntity findById(@PathVariable("id") final Integer id
            , @RequestHeader(value = "Content-type", defaultValue = MediaType.APPLICATION_JSON_VALUE) final String contentType) {

        Optional<Product> product = productBusiness.findById(id);
        if (product.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(product);
    }

    @RequestMapping(value = "/products/{id}/inativar", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH

    )
    public ResponseEntity inativar(@PathVariable("id") final Integer id
            ,
                                   @RequestBody(required = false) ProductTransport productTransport
    ) {

        Optional<Product> product = productBusiness.inativarProduto(id);
        if (product.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(product);
    }
}
