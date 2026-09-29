# ADR-003 — Separate Object Construction from Reading Represented Data

## Status

Accepted

## Context

Forma objects may represent information they do not own or materialize. A structurally immutable `Data` can be backed by a source whose contents change independently.

Requiring constructors to read that source confuses two different guarantees: integrity of the object being constructed and facts about represented information at a particular moment.

For example, `FieldAt(reference, data)` can be a valid object even when the referenced field is currently absent. Likewise, an `AttributeValue` can validly represent the value of an attribute in a model before that value is requested.

## Decision

> **Construction establishes object integrity; reading establishes facts about represented data.**

### Construction validates owned state

Constructors establish invariants of the collaborators and configuration that constitute the object itself.

Examples include a valid `AttributeName`, a valid positional reference, or a `ModelOf` composed from `Metadata`, `Data`, and `FieldMapping`.

Construction does not read represented values merely to prove transient facts about an external or mutable source.

### Model is an instance of Metadata over Data

```java
new ModelOf(metadata, data, mapping)
```

constructs the relationship. It does not interpret every metadata attribute.

`Model` exposes `AttributeValue` objects. Their semantic values are established when `value()` is requested.

```text
Metadata ─────┐
Data ─────────┼── Model
FieldMapping ─┘
                  │
                  ▼
           AttributeValue<T>
                  │ value()
                  ▼
          FieldReference
                  │
                  ▼
               FieldAt
                  │
                  ▼
          Attribute.valueFrom
                  │
                  ▼
                  T
```

> **An Attribute describes a semantic value. An AttributeValue represents that value in a Model.**

### Failures use the vocabulary of the abstraction that detects them

Local failures are explicit Java exceptions:

```text
MissingFieldException
UnparsableValueException
RejectedValueException
```

A `FieldAt` may raise `MissingFieldException`. A represented value may raise `UnparsableValueException` when it cannot provide the requested primitive form. An attribute constraint may raise `RejectedValueException`.

When `AttributeValue.value()` cannot establish its semantic value, `AttributeValueException` adds the semantic `AttributeName` and representation `FieldReference` while preserving the lower-level exception as its cause.

> **Each abstraction reports failures in its own vocabulary; a higher-level abstraction adds context without erasing the lower-level cause.**

### Demand-driven reading does not introduce mutable validity state

Forma does not introduce an internal lifecycle such as `unvalidated -> validated`. Objects remain immutable compositions of collaborators. Calling behavior may read represented state and may fail without mutating the object.

Caching and snapshot semantics are separate capabilities. If stable values over a mutable source are required, that stability must be represented explicitly rather than being an accidental consequence of construction.

## Consequences

Constructing a model does not interpret field values.

Iterating a model or asking an `AttributeValue` for its name does not by itself require its represented field to be read.

Requesting one attribute value reads only the information necessary for that value.

Unused fields remain represented by `Data` without being promoted into semantic Java state.

A constructor guarantees the integrity of the object, not the current state of the external world represented through it.

## Alternatives considered

Eagerly validating every metadata attribute during model construction was rejected because it performs semantic work during construction, reads values that may never be needed, and cannot provide durable validity over mutable data without also capturing a snapshot.

Making `Model` mutable and validating on first use was rejected because demand-driven reading requires no mutable validity state.

Universal eager snapshots were rejected because Forma must also represent external, partial, large, streaming, and otherwise non-materialized information.

## Relationship to ADR-001

ADR-001 establishes `Data` as first-class represented information and `Metadata` as first-class semantic knowledge. This ADR specifies when represented information is read.

The fundamental separation is:

> **Data knows representation. Metadata knows semantics. FieldMapping relates their coordinates.**
