# ADR-002 — Keep Semantic Names Distinct from Representation Coordinates

## Status

Accepted

## Context

Forma separates represented information from semantic meaning. A `Data` object must represent information without knowing which `Metadata` may interpret it, while an `Attribute<T>` must describe meaning without knowing whether a representation addresses fields by names, columns, positions, paths, members, or another coordinate system.

The same semantic attribute may therefore correspond to different representation coordinates.

## Decision

### AttributeName is semantic identity within Metadata

```java
public interface AttributeName<T> extends Text {
}
```

An `AttributeName` identifies an attribute within a `Metadata`, not globally across the application.

### FieldReference is an opaque representation coordinate

```java
public interface FieldReference {
}
```

Concrete representations may use named, positional, hierarchical, column-based, member-based, or other references.

> **Forma core knows that data has coordinates, not what shape those coordinates have.**

### Field carries its representation coordinate

```java
public interface Field {
    FieldReference reference();
    FieldValue value();
}
```

A `Field` is an addressable portion of represented `Data`.

```java
public interface Data extends Iterable<Field> {
}
```

Generic lookup is derived from these contracts. `FieldAt` represents the field at a particular coordinate:

```java
new FieldAt(reference, data)
```

### Attribute lookup remains a semantic concern

`Metadata` remains an iterable description rather than acquiring a repository-style lookup method. Generic semantic lookup is represented by `AttributeAt`:

```java
new AttributeAt<>(name, metadata)
```

This mirrors representation lookup without collapsing the two coordinate systems:

```text
Metadata + AttributeName  -> AttributeAt -> Attribute
Data     + FieldReference -> FieldAt     -> Field
```

The symmetry is structural, while the coordinates remain semantically distinct.

### FieldMapping relates semantic names to representation coordinates

```java
public interface FieldMapping {
    FieldReference reference(AttributeName<?> attribute);
}
```

The relationship is:

```text
AttributeName
     │
     │ FieldMapping
     ▼
FieldReference
     │
     │ FieldAt
     ▼
   Field
     │
     │ Attribute.valueFrom(...)
     ▼
semantic value
```

> **Data knows representation. Metadata knows semantics. FieldMapping relates their coordinates.**

### Same-name mapping is a convention

`SameNameMapping` derives a representation-specific `FieldReference` from the text of an `AttributeName`. Equal text is a mapping convention, not a universal coordinate type.

## Consequences

Semantic names and representation coordinates remain independent even when they contain the same text.

The same `Metadata` can be applied to differently represented `Data` through different `FieldMapping` objects. The same `Data` can participate in different models.

Representation-specific coordinate systems remain outside Forma core.

## Alternatives considered

Using `String` directly for semantic names was rejected because `AttributeName<T>` carries semantic type and construction invariants.

Treating `AttributeName` as global identity was rejected because `Metadata` supplies its semantic context.

Letting `Data` accept `AttributeName` directly was rejected because representation would become coupled to semantics.

Letting `Attribute` own a `FieldReference` was rejected because semantics would become coupled to one representation layout.

Defining a universal textual `FieldReference` was rejected because names have no privileged status over positions, paths, columns, members, or future coordinate systems.
