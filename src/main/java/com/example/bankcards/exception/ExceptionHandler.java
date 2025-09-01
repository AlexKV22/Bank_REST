package com.example.bankcards.exception;

import com.example.bankcards.util.CardMaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(CardExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleCardExistsException(CardExistsException e) {
        log.warn("Карта с номером {} уже существует", CardMaskUtil.mask(e.getCardNumber()));
        return ResponseEntity.badRequest().body("Такая карта уже существует");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleUserExistException(UserExistException e) {
        log.warn("Пользователь с именем {} уже существует", e.getName());
        return ResponseEntity.badRequest().body("Имя пользователя занято");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(CardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException e) {
        log.warn("Карта с номером {} не найдена в списке карт пользователя {}", CardMaskUtil.mask(e.getCardNumber()), e.getName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Карта не найдена");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("Пользователь с именем {} не найден", e.getName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден в базе данных");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleRoleNotFoundException(RoleNotFoundException e) {
        log.warn("Роль {} не найдена", e.getRoleName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Роль не найдена");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(EmptyDatabaseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEmptyDatabaseException(EmptyDatabaseException e) {
        log.warn("Полученный список из базы данных пустой");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalCardStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalCardStatusException(IllegalCardStatusException e) {
        log.warn("Ошибка изменения статуса карты с id: {}, текущий статус: {}, попытка установить статус: {}", e.getId(), e.getStatusCard(), e.getUpdateStatusCard());
        return ResponseEntity.badRequest().body("Ошибка изменения статуса карты, проверьте текущий статус");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalUserException(IllegalUserException e) {
        log.warn("Карта с id {} не принадлежит пользователю с именем {}", e.getId(), e.getUsername());
        return ResponseEntity.badRequest().body("Карта не принадлежит пользователю");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidBalanceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleInvalidBalanceException(InvalidBalanceException e) {
        log.warn("Недостаточно среств для перевода у карты с номером: {}.", CardMaskUtil.mask(e.getCardNumber()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Недостаточно среств для перевода");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleInvalidJwtException(InvalidJwtException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Неверный JWT токен при валидации");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> validateException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> uniqueFieldException(DataAccessException e) {
        log.warn("Ошибка выполнения операции, проверьте доступ к базе данных.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
