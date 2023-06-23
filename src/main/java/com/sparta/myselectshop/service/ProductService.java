package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.*;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    private final ProductFolderRepository productFolderRepository;

    public static final int MIN_MY_PRICE = 100; // myPrice 가 100 이상이여야 하는 조건을 판별할 상수

    
    
    // 관심 상품으로 등록하기 위해 product Table에 저장하는 메서드
    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        // 생성 요청 받아온 데이터를 Entity 객체로 만들어 주고, DB 에도 저장한다.
        Product product = productRepository.save(new Product(requestDto,user));
        return new ProductResponseDto(product); // 브라우저에 response 를 보내기 위해 Dto 타입다시 생성
    } 


    
    
    // myPrice 칼럼 수정하는 메서드
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

    
    
    @Transactional(readOnly = true)
    // 해당 유저가 관심 상품으로 등록한 목록 출력하는 메서드
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc  ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction,sortBy); // 에를 들어서 id 기준으로 내림차순이라면 Sort.by(Sort.Direction.DESC,id);
        Pageable pageable = PageRequest.of(page,size,sort);

        UserRoleEnum userRoleEnum = user.getRole();

        Page<Product> productList;

        if(userRoleEnum == UserRoleEnum.USER){
            productList = productRepository.findAllByUser(user, pageable);
        } else {
            productList = productRepository.findAll(pageable);
        }

        // page 가 convert 하는 메서드 제공
        return productList.map(ProductResponseDto::new);
    }



    // 해당 상품 데이터 찾아서 lPrice 수정하는 메서드
    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 상품은 존재하지 않습니다.")
        );
        product.updateByItemDto(itemDto);
    }


    // 상품을 폴더에 추가하는 메서드
    public void addFolder(Long productId, Long folderId, User user) {
        // 폴더랑 상품이 해당 DB에 존재하는지 확인
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("해당 상품이 존재하지 않습니다.")
        );
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("해당 폴더가 존재하지 않습니다.")
        );

        // 위에 폴더와 상품이 유저 데이터에 해당하는지
        if ( !product.getUser().getId().equals(user.getId()) || !folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("회원님의 관심상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }

        // 같은 폴더에 같은 상품을 중복으로 추가하는지
        Optional<ProductFolder> overlapFolder =  productFolderRepository.findByProductAndFolder(product,folder);
        if (overlapFolder.isPresent()) { // 존재하면
            throw new IllegalArgumentException("중복된 폴더입니다");
        }

        // 드디어 등록할 수 있음.
        productFolderRepository.save(new ProductFolder(product,folder));
    }// end of addFolder()


    public Page<ProductResponseDto> getProductsInFolder(
            Long folderId, int page, int size, String sortBy, boolean isAsc, User user) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        // 에를 들어서 id 기준으로 내림차순이라면 Sort.by(Sort.Direction.DESC,id);
        Sort sort = Sort.by(direction,sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);


        // 해당 폴더에 등록된 상품 가져오기
        Page<Product> productList =
                productRepository.findAllByUserAndProductFolderList_FolderId(user,folderId,pageable);

        // Dto 타입으로 변환하여 반환함.
        return productList.map(ProductResponseDto::new);
    }
}
