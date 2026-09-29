# ADR-003 — Separate Object Construction from Reading Represented Data

## Status

Accepted

## Context

Forma objects may represent information they do not own or materialize. A structurally immutable `Data` can be backed by a source whose contents change independently.

Requiring constructors to read that source confuses two different guarantees: integrity of the object being constructed and facts about represented information at a particular moment.

For example, `FieldAt(reference, data)` can be a valid object even when the referenced field is currently absent. Likewise, a `Model` can validly relate `Metadata` to `Data` before any semantic value is requested.

## Decision

> **Construction establishes object integrity; reading establishes facts about represented data.**

### Construction validates owned state

Constructors establish invariants of the collaborators and configuration that constitute the object itself.

Examples include a valid `AttributeName`, a valid positional reference, or a `ModelOf` composed from `Metadata`, `Data`, and `FieldMapping`.

Construction does not read represented values merely to prove transient facts about an external or mutable source.

### Model relates Metadata to Data

```java
new ModelOf(metadata, data, mapping)
```

constructs the relationship. It does not interpret every metadata attribute.

`Data` exposes representation structure through `Field`. `Metadata` exposes semantic structure through `Attribute`. `Model` does not introduce a third iterable structure; it establishes the semantic value requested by name:

```java
<T> T valueOf(AttributeName<T> name);
```

The resolution path is:

```text
AttributeName<T>
      │
      ├───────────────┐
      ▼               │
 AttributeAt<T>       │ FieldMapping
      │               ▼
      │          FieldReference
      │               │
      │               ▼
      │            FieldAt
      │               │
      └──── valueFrom ┘
              │
              ▼
              T
```

`AttributeAt` models semantic lookup inside `Metadata` just as `FieldAt` models representation lookup inside `Data`.

> **Data exposes representation structure. Metadata exposes semantic structure. Model relates them; it does not define another structure.**

### Failures use the vocabulary of the abstraction that detects them

Local failures are explicit Java exceptions:

```text
MissingFieldException
UnparsableValueException
RejectedValueException
```

A `FieldAt` may raise `MissingFieldException`. A represented value may raise `UnparsableValueException` when it cannot provide the requested primitive form. An attribute constraint may raise `RejectedValueException`.

When `Model.valueOf(...)` cannot establish a semantic value from represented data, `AttributeValueException` adds the semantic `AttributeName` and representation `FieldReference` while preserving the lower-level exception as its cause.

Failure to find the requested semantic attribute in `Metadata` remains an attribute lookup failure rather than being disguised as a represented-data failure.

> **Each abstraction reports failures in its own vocabulary; a higher-level abstraction adds context without erasing the lower-level cause.**

### Demand-driven reading does not introduce mutable validity state

Forma does not introduce an internal lifecycle such as `unvalidated -> validated`. Objects remain immutable compositions of collaborators. Calling behavior may read represented state and may fail without mutating the object.

Caching and snapshot semantics are separate capabilities. If stable values over a mutable source are required, that stability must be represented explicitly rather than being an accidental consequence of construction.

## Consequences

Constructing a model does not interpret field values.

Inspecting `model.metadata()` does not resolve field mappings or represented values.

Requesting one value through `Model.valueOf(...)` reads only the information necessary for that semantic value.

Consumers that need model structure inspect `Metadata`; they do not iterate a second collection of model values.

Unused fields remain represented by `Data` without being promoted into semantic Java state.

A constructor guarantees the integrity of the object, not the current state of the external world represented through it.

## Alternatives considered

### Model as Iterable<AttributeValue<?>>

Rejected because it duplicates the structure already exposed by `Metadata`. `AttributeValue` also reduced to a lazy pairing of an attribute name and a value computation rather than carrying an independent domain responsibility.

Consumers that need to enumerate semantic structure can iterate `model.metadata()` and request only the values they actually need.

### Lookup directly on Metadata

A method such as `Metadata.attribute(name)` was not added to the fundamental contract. Generic lookup is instead represented by `AttributeAt`, preserving the same object-oriented structure used by `FieldAt` over `Data`.

### Eager model validation

Rejected because it performs semantic work during construction, reads values that may never be needed, and cannot provide durable validity over mutable data without also capturing a snapshot.

### Mutable validation state

Rejected because demand-driven reading requires no mutable validity state.

### Universal eager snapshots

Rejected because Forma must also represent external, partial, large, streaming, and otherwise non-materialized information.

## Relationship to ADR-001

ADR-001 establishes `Data` as first-class represented information and `Metadata` as first-class semantic knowledge. This ADR specifies when represented information is read and clarifies that `Model` is their semantic relationship rather than a third structural collection.

The fundamental separation is:

> **Data knows representation. Metadata knows semantics. FieldMapping relates their coordinates.**
