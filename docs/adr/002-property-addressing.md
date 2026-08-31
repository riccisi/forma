# ADR-002 — Keep Property Coordinates Opaque in Forma Core

## Status

Accepted

## Context

Forma separates represented information from semantic interpretation.

A `Data` object must be able to exist independently from any semantic `Metadata` or `Model` that may later interpret it. At the same time, `Metadata` must be able to bind a semantic `Attribute<T>` to the correct `Property` without assuming that a semantic attribute identity is equal to a textual source name.

A semantic coordinate may correspond to very different representation coordinates depending on the source:

```text
Student.email -> JSON field "e_mail_address"
Student.email -> JDBC column "EMAIL_ADDR"
Student.email -> POJO member "emailAddress"
Student.email -> positional value 7
Student.email -> nested path ["contact", "email"]
```

The core model therefore needs a representation-neutral addressing boundary.

ADR-001 originally introduced both `PropertyReference` and the possible specialization `PropertyName`. Further experimentation showed that the specialization is unnecessary in the core model and risks privileging named representations.

## Decision

Forma core uses a single opaque coordinate abstraction:

```java
public interface PropertyReference {
}
```

`Data` resolves properties only through representation coordinates:

```java
public interface Data extends Iterable<Property> {

    Property property(PropertyReference reference);
}
```

Semantic identities remain distinct:

```java
public interface AttributeName<T> {
}
```

The association between the two coordinate systems belongs to the binding relationship and is represented by `PropertyMapping`:

```java
public interface PropertyMapping {

    PropertyReference property(AttributeName<?> attribute);
}
```

Binding therefore follows this path:

```text
AttributeName
     |
     v
PropertyMapping
     |
     v
PropertyReference
     |
     v
    Data
     |
     v
  Property
```

The mapping belongs neither to `Data` nor to `Attribute` as intrinsic state. It is contextual information supplied when semantic metadata is bound to a concrete representation.

> **Data knows representation. Metadata knows semantics. PropertyMapping relates their coordinates.**

### PropertyReference is deliberately opaque

Forma core does not classify representation coordinates as names, positions, paths, columns, members, or any other concrete shape.

Representation-specific code may define whichever coordinates naturally describe its source:

```java
record JsonField(String name) implements PropertyReference {}
record JdbcColumn(String name) implements PropertyReference {}
record Position(int index) implements PropertyReference {}
record PropertyPath(List<String> segments) implements PropertyReference {}
```

These examples are representation concepts, not Forma core concepts.

> **Forma core knows that data has coordinates, not what shape those coordinates have.**

### PropertyName is removed from core

`PropertyName` does not establish an invariant or behavior beyond `PropertyReference`. Its presence in core would classify one particular coordinate shape without providing value to the binding contract.

A named coordinate has no privileged status over a positional or hierarchical coordinate.

Therefore Forma core does not define:

```java
interface PropertyName extends PropertyReference {
}
```

A representation-specific module remains free to introduce a named coordinate object if that representation benefits from one.

### The same objects may participate in different bindings

Because mapping belongs to the binding relationship, the same `Metadata` may bind differently represented `Data` by using different `PropertyMapping` objects.

Likewise, the same `Data` may participate in different semantic bindings without acquiring knowledge of those semantic models.

This is intentional. Neither side owns the relationship by itself.

## Evidence from the spike

The contract tests exercise the same core API with multiple coordinate shapes:

* a named reference whose text does not equal the semantic attribute identity;
* a positional reference;
* a nested path reference;
* different mappings applied to the same represented data.

No change to the `Data`, `Metadata`, `Attribute`, or `PropertyMapping` contracts is required to support those cases.

The experiment therefore demonstrates that `PropertyReference` is sufficient as the core addressing abstraction and that more specific coordinate types can remain representation-local.

## Consequences

Forma core does not depend on JSON fields, JDBC columns, POJO members, paths, positions, or naming conventions.

Semantic attributes remain independent from physical representation layouts.

Named, positional, hierarchical, column-oriented, and future coordinate systems can participate in the same binding model.

The binding caller must provide a `PropertyMapping`. This is deliberate because the association between semantics and representation is contextual information rather than intrinsic state of either side.

Convention-based mappings such as same-name or camelCase-to-snake_case remain possible, but those conventions must produce representation-specific `PropertyReference` objects rather than becoming assumptions of the core model.

## Alternatives considered

### Data accepts AttributeName directly

Rejected because arbitrary represented data would become coupled to semantic identities belonging to models it may never participate in.

### Attribute owns a PropertyReference

Rejected because semantic attributes would become coupled to physical representation layouts.

### Metadata permanently owns the mapping

Rejected as the fundamental model because the same metadata may be applied to differently represented data. Mapping belongs to a particular binding relationship.

### PropertyName in core

Rejected because a textual name is only one possible coordinate shape and adds no core invariant beyond `PropertyReference`.

### A hierarchy for every coordinate kind

Rejected because the core does not need to enumerate or understand coordinate shapes in order to perform binding.

## Relationship with ADR-001

This ADR preserves the representation/semantics separation established by ADR-001 and refines its property-addressing decision.

Where ADR-001 presents `PropertyName` as a possible core specialization of `PropertyReference`, this ADR supersedes that part of the earlier decision: Forma core retains only the opaque `PropertyReference` abstraction.
