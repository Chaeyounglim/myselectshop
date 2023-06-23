package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findAllByUser(User user, Pageable pageable);

    Page<Product> findAllByUserAndProductFolderList_FolderId(User user, Long folderId, Pageable pageable);
    // 현재 로그인한 유저가 등록한 폴더에 속해있는 상품 리스트를 가져와야 함.
}




