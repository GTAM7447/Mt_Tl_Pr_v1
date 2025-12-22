package com.spring.jwt.Document;

import com.spring.jwt.Enums.DocumentType;
import com.spring.jwt.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    /**
     * Find document by user ID and document type
     */
    Optional<Document> findByUserIdAndDocumentType(Integer userId, DocumentType documentType);

    /**
     * Find all documents by user ID
     */
    List<Document> findByUserIdOrderByUploadedAtDesc(Integer userId);

    /**
     * Find documents by user ID with pagination support
     */
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId ORDER BY d.uploadedAt DESC")
    Page<Document> findDocumentsByUserIdWithPagination(@Param("userId") Integer userId, Pageable pageable);

    /**
     * Find documents by user ID (without pagination)
     */
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId ORDER BY d.uploadedAt DESC")
    List<Document> findDocumentsByUserId(@Param("userId") Integer userId);

    /**
     * Check if document exists for user and document type
     */
    boolean existsByUserIdAndDocumentType(Integer userId, DocumentType documentType);

    /**
     * Count documents by user ID
     */
    long countByUserId(Integer userId);

    /**
     * Find documents by user ID and document types
     */
    List<Document> findByUserIdAndDocumentTypeIn(Integer userId, List<DocumentType> documentTypes);

    /**
     * Delete document by user ID and document type
     */
    void deleteByUserIdAndDocumentType(Integer userId, DocumentType documentType);
}
