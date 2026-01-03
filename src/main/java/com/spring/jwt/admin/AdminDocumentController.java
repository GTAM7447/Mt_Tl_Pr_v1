package com.spring.jwt.admin;

import com.spring.jwt.dto.DocumentResponseDTO;
import com.spring.jwt.dto.DocumentDetailResponseDTO;
import com.spring.jwt.Document.DocumentService;
import com.spring.jwt.Enums.DocumentType;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/documents")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Document Management", description = "Admin operations for user document management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDocumentController {

    private final DocumentService documentService;

    @Operation(
        summary = "Upload document for user (Admin)",
        description = "Admin can upload documents for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Document uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}/upload")
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Document file", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Document type", required = true)
            @RequestParam("documentType") DocumentType documentType,
            @Parameter(description = "Document description")
            @RequestParam(value = "description", required = false) String description) {
        
        log.info("Admin uploading document for user ID: {}, type: {}", userId, documentType);
        DocumentResponseDTO response = documentService.uploadDocument(userId, file, documentType, description);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Create document record for user (Admin)",
        description = "Admin can create document records for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Document record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<String>> createDocument(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Document type", required = true)
            @RequestParam("documentType") DocumentType documentType,
            @Parameter(description = "Document description")
            @RequestParam(value = "description", required = false) String description) {
        
        log.info("Admin creating document record for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Document record creation not implemented", 
            "This endpoint would create a document record for user " + userId);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Get documents by user ID (Admin)",
        description = "Admin can retrieve all documents for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving documents for user ID: {}", userId);
        List<DocumentResponseDTO> response = documentService.getAllDocumentsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get document by ID (Admin)",
        description = "Admin can retrieve any document by ID with full details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/{documentId}/user/{userId}")
    public ResponseEntity<DocumentDetailResponseDTO> getDocumentById(
            @Parameter(description = "Document ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid document ID") Integer documentId,
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving document by ID: {} for user: {}", documentId, userId);
        DocumentDetailResponseDTO response = documentService.getDocumentById(userId, documentId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update document (Admin)",
        description = "Admin can update any document with new file or description"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/{documentId}/user/{userId}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(
            @Parameter(description = "Document ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid document ID") Integer documentId,
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "New document file (optional)")
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "Document description (optional)")
            @RequestParam(value = "description", required = false) String description) {
        
        log.info("Admin updating document ID: {} for user: {}", documentId, userId);
        DocumentResponseDTO response = documentService.updateDocument(userId, documentId, file, description);
        return ResponseEntity.ok(response);
    }

//    @Operation(
//        summary = "Get documents with pagination (Admin)",
//        description = "Admin can retrieve documents for a user with pagination"
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
//        @ApiResponse(responseCode = "404", description = "User not found"),
//        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
//    })
//    @GetMapping("/user/{userId}/paginated")
//    public ResponseEntity<PaginatedDocumentResponseDTO> getDocumentsPaginated(
//            @Parameter(description = "User ID", required = true)
//            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
//            @Parameter(description = "Page number (0-based)")
//            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
//            @Parameter(description = "Page size")
//            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size) {
//
//        log.info("Admin retrieving paginated documents for user ID: {} - page: {}, size: {}", userId, page, size);
//        PaginatedDocumentResponseDTO response = documentService.getDocumentsPaginated(userId, page, size);
//        return ResponseEntity.ok(response);
//    }

    @Operation(
        summary = "Delete document (Admin)",
        description = "Admin can delete any document"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/{documentId}/user/{userId}")
    public ResponseEntity<ResponseDto<String>> deleteDocument(
            @Parameter(description = "Document ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid document ID") Integer documentId,
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin deleting document ID: {} for user: {}", documentId, userId);
        documentService.deleteDocument(userId, documentId);
        ResponseDto<String> response = ResponseDto.success("Document deleted successfully", 
            "Document with ID " + documentId + " has been deleted");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get document by type (Admin)",
        description = "Admin can retrieve document by type for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}/type/{documentType}")
    public ResponseEntity<DocumentDetailResponseDTO> getDocumentByType(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Document type", required = true)
            @PathVariable DocumentType documentType) {
        
        log.info("Admin retrieving document of type: {} for user: {}", documentType, userId);
        DocumentDetailResponseDTO response = documentService.getDocumentByType(userId, documentType);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Replace document by type (Admin)",
        description = "Admin can replace existing document of same type for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document replaced successfully"),
        @ApiResponse(responseCode = "201", description = "New document created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/user/{userId}/replace")
    public ResponseEntity<DocumentResponseDTO> replaceDocument(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Document file", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Document type", required = true)
            @RequestParam("documentType") DocumentType documentType,
            @Parameter(description = "Document description")
            @RequestParam(value = "description", required = false) String description) {
        
        log.info("Admin replacing document of type: {} for user: {}", documentType, userId);
        DocumentResponseDTO response = documentService.replaceDocument(userId, file, documentType, description);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Download document (Admin)",
        description = "Admin can download any document file"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document downloaded successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/{documentId}/user/{userId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @Parameter(description = "Document ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid document ID") Integer documentId,
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin downloading document ID: {} for user: {}", documentId, userId);
        byte[] fileData = documentService.downloadDocument(userId, documentId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/octet-stream")
                .header("Content-Disposition", "attachment; filename=\"document_" + documentId + "\"")
                .body(fileData);
    }

    @Operation(
        summary = "Get document count for user (Admin)",
        description = "Admin can get total document count for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document count retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ResponseDto<Long>> getDocumentCount(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving document count for user: {}", userId);
        long count = documentService.getDocumentCount(userId);
        ResponseDto<Long> response = ResponseDto.success("Document count retrieved successfully", count);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Check if document exists (Admin)",
        description = "Admin can check if document of specific type exists for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document existence checked successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}/exists/{documentType}")
    public ResponseEntity<ResponseDto<Boolean>> documentExists(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Document type", required = true)
            @PathVariable DocumentType documentType) {
        
        log.info("Admin checking if document type: {} exists for user: {}", documentType, userId);
        boolean exists = documentService.documentExists(userId, documentType);
        ResponseDto<Boolean> response = ResponseDto.success("Document existence checked successfully", exists);
        return ResponseEntity.ok(response);
    }
}