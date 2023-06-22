package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public static final int MIN_MY_PRICE = 100; // myPrice 가 100 이상이여야 하는 조건을 판별할 상수

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        // 생성 요청 받아온 데이터를 Entity 객체로 만들어 주고, DB 에도 저장한다.
        Product product = productRepository.save(new Product(requestDto));
        return new ProductResponseDto(product); // 브라우저에 response 를 보내기 위해 Dto 타입다시 생성
    } // 관심 상품으로 등록하기 위해 product Table에 저장하는 메서드


    // 수정을 할 경우에는 반드시 Transactional 환경이여야 한다.
    // 그래야 spring JPA 에서 변경 감지가 되어서 DB에 내용도 같이 수정합니다.
    @Transactional
    public ProductResponseDto updateProduct(ProductMypriceRequestDto requestDto, Long id) {
        int myPrice = requestDto.getMyprice();

        if(myPrice < MIN_MY_PRICE){
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 "
                    + MIN_MY_PRICE + "원 이상이여야 합니다.");
        }

        // DB에 전달받은 id에 따른 데이터가 있으면 Product Entity 에 저장하고, 없을 경우 null 예외 던짐
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 상품을 찾을 수 없습니다. ")
        );

        product.update(requestDto); // 수정할 데이터를 넘겨줘서 해당 product 멤버 필드 내용 수정.

        return new ProductResponseDto(product);
    }


    public List<ProductResponseDto> getProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponseDto> responseDtoList = new ArrayList<>();

        for (Product product : productList) {
            responseDtoList.add(new ProductResponseDto(product));
        }

        return responseDtoList;
    }
}
