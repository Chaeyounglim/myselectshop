package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        // 생성 요청 받아온 데이터를 Entity 객체로 만들어 주고, DB 에도 저장한다.
        Product product = productRepository.save(new Product(requestDto));
        return new ProductResponseDto(product); // 브라우저에 response 를 보내기 위해 Dto 타입다시 생성
    }



}
