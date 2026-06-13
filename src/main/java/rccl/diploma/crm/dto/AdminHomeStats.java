package rccl.diploma.crm.dto;

public record AdminHomeStats(
        long requestsCount,
        long inWorkCount,
        long newCount,
        long rejectedCount,
        long usersCount,
        long buildingsCount,
        long newsCount
) {}
