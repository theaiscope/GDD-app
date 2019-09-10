# 4. Decouple domain models from representation

Date: 2019-09-10

## Status

Accepted

## Context

Domain models were used as-is for persistence and over-the-wire communication, making it difficult
to define a proper and strong domain model.

## Decision

Separate DTOs should be defined for persistence and over-the-wire communication, which can have a
more adequate representation for their use-case, without affecting our domain model.

## Consequences

The domain models can better follow the ubiquituos language, and we also avoid having an anemic domain.

