package com.policene.voluntech.repositories.specs;

import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.enums.CampaignStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class CampaignSpecs {

    public static Specification<Campaign> nameLike(String name) {
        return (root, query, cb) ->
                // Fazemos a comparação LIKE e deixamos ambos valores em LowerCase.
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Campaign> hasMinAmount(Double minAmount) {
        return (root, query, cb) ->
                cb.ge(root.get("goalAmount"), minAmount);
    }

    public static Specification<Campaign> hasMaxAmount(Double maxAmount) {
        return (root, query, cb) ->
                cb.le(root.get("goalAmount"), maxAmount);
    }

    public static Specification<Campaign> organizationLike(String organization) {
        return (root, query, cb) -> {
            Join<Object, Object> joinOrganization = root.join("organization", JoinType.LEFT);
            return cb.like(cb.upper(joinOrganization.get("organizationName")), "%" + organization.toUpperCase() + "%");
        };
    }

    public static Specification<Campaign> hasStatusApproved() {
        return (root, query, cb) -> {
             return cb.equal(root.get("status"), CampaignStatus.APPROVED);
        };
    }


}
