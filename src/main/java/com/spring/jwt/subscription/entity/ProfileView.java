package com.spring.jwt.subscription.entity;

import com.spring.jwt.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity for tracking profile views and credit deductions.
 * Records who viewed whose profile and associated costs.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Entity
@Table(name = "profile_views", indexes = {
    @Index(name = "idx_viewer_id", columnList = "viewer_id"),
    @Index(name = "idx_viewed_profile_id", columnList = "viewed_profile_id"),
    @Index(name = "idx_viewed_at", columnList = "viewed_at"),
    @Index(name = "idx_contact_revealed", columnList = "contact_revealed")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id")
    private Long viewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profile_view_viewer"))
    private User viewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewed_profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profile_view_viewed"))
    private User viewedProfile;

    @Column(name = "viewed_at", nullable = false)
    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();

    @Column(name = "credits_deducted")
    @Builder.Default
    private Integer creditsDeducted = 0;

    @Column(name = "contact_revealed")
    @Builder.Default
    private Boolean contactRevealed = false;

    @Column(name = "contact_revealed_at")
    private LocalDateTime contactRevealedAt;

    @Column(name = "contact_reveal_credits")
    @Builder.Default
    private Integer contactRevealCredits = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_subscription_id", foreignKey = @ForeignKey(name = "fk_profile_view_subscription"))
    private UserSubscription userSubscription;

    // ========== BUSINESS METHODS ==========

    /**
     * Mark contact as revealed
     */
    public void revealContact(int credits) {
        this.contactRevealed = true;
        this.contactRevealedAt = LocalDateTime.now();
        this.contactRevealCredits = credits;
    }

    /**
     * Get total credits used for this view
     */
    public int getTotalCreditsUsed() {
        return (creditsDeducted != null ? creditsDeducted : 0) + 
               (contactRevealCredits != null ? contactRevealCredits : 0);
    }
}
