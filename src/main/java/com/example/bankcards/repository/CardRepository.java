package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Boolean existsByNumber(String cardNumber);
    Page<Card> findCardsByUserName(String name, Pageable pageable);
    Page<Card> findAll(Pageable pageable);
    Optional<Card> findCardByNumberAndUserName(String cardNumber, String userName);
    Optional<Card> findCardByIdAndUserName(Long id, String userName);
}
