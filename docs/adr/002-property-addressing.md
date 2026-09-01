# ADR-002 — Keep Semantic Names Distinct from Representation Coordinates

## Status

Accepted

## Context

Forma separates represented information from semantic interpretation.

A `Data` object must be able to represent information without knowing which semantic `Metadata` may later interpret it. Conversely, semantic `Attribute<T>` objects must be able to describe meaning without knowing whether a concrete representation addresses information through JSON fields, JDBC columns, object members, positions, paths, or another coordinate system.

The same semantic attribute may therefore be associated with different representation coordinates:

```text
email -> JSON field "e_mail_address"
email -> JDBC column "EMAIL_ADDR"
email -> POJO member "emailAddress"
email -> positional value 7
email -> nested path ["contact", "email"]
```

Forma needs both a semantic name and a representation coordinate, but those concepts must remain distinct.

## Decision

### AttributeName is a typed semantic value object

An attribute has a textual semantic name inside a `Metadata`.

```java
public interface AttributeName<T> extends Text {
}
```

The generic parameter expresses the semantic value type associated with that name at the API boundary.

A concrete attribute name is a value object. Equal textual names represent the same attribute name within a metadata definition. The name is validated when constructed so invalid textual states are not representable.

For example:

```java
AttributeName<String> NAME = new AttributeNameOf<>("name");
AttributeName<Integer> AGE = new AttributeNameOf<>("age");
```

`AttributeName` is not globally scoped application identity. Its semantic context is the `Metadata` that contains the corresponding attribute.

> **An AttributeName identifies an attribute within Metadata, not globally across the application.**

### PropertyReference is an opaque representation coordinate

Representation addressing uses a separate abstraction:

```java
public interface PropertyReference {
}
```

Forma core deliberately does not prescribe the shape of a property reference.

Concrete representations may define coordinates such as:

```java
record JsonField(String name) implements PropertyReference {}
record JdbcColumn(String name) implements PropertyReference {}
record Position(int index) implements PropertyReference {}
record PropertyPath(List<String> segments) implements PropertyReference {}
```

These are representation concepts rather than semantic attribute names.

> **Forma core knows that data has coordinates, not what shape those coordinates have.**

### Property carries its representation coordinate

A property is an addressable portion of represented data:

```java
public interface Property {

    PropertyReference reference();

    PropertyValue value();
}
```

`Data` therefore only needs to expose its represented properties:

```java
public interface Data extends Iterable<Property> {
}
```

Generic lookup is derived from these contracts rather than being prescribed as a fundamental `Data` operation. `PropertyAt` represents a property located at a particular coordinate inside `Data`:

```java
new PropertyAt(reference, data)
```

This keeps lookup compositional while allowing specialized representations to optimize their own access patterns when needed.

### PropertyMapping relates semantic names to representation coordinates

The association between semantic names and representation coordinates belongs to the binding relationship:

```java
public interface PropertyMapping {

    PropertyReference property(AttributeName<?> attribute);
}
```

Model construction therefore follows this path:

```text
Metadata
   |
   v
AttributeName
   |
   v
PropertyMapping
   |
   v
PropertyReference
   |
   v
PropertyAt(reference, Data)
   |
   v
Attribute.from(Property)
   |
   v
ModelAttribute
```

Neither `Data` nor `Attribute` owns this relationship by itself.

> **Data knows representation. Metadata knows semantics. PropertyMapping relates their coordinates.**

### Same-name mapping is a convention, not a coordinate type

Many representations use property names that naturally match semantic attribute names. Forma supports this as a mapping convention without introducing a universal textual `PropertyReference`.

```java
new SameNameMapping(JsonField::new)
new SameNameMapping(JdbcColumn::new)
```

The semantic name supplies the text while the representation-specific function creates the appropriate coordinate object.

This distinction is intentional:

> **Same name is a mapping convention, not a universal representation coordinate.**

A positional or hierarchical representation can use a completely different `PropertyMapping` without changing `Attribute`, `Metadata`, `Data`, or `Model`.

## Consequences

Semantic attributes have simple, typed, textual names with value semantics.

A semantic name remains distinct from a physical representation coordinate even when both happen to contain the same text.

Different metadata definitions may independently use the same attribute names because names are interpreted within their metadata context rather than as global identifiers.

Representation-specific coordinate systems remain outside the Forma core vocabulary.

The same `Metadata` may be associated with differently represented `Data` through different `PropertyMapping` objects, and the same `Data` may participate in different semantic bindings.

Convention-based mappings remain concise while explicit mappings continue to support renamed, positional, hierarchical, and otherwise heterogeneous representations.

## Alternatives considered

### Use String directly for semantic attribute names

Rejected because an attribute name has semantic type and construction invariants that a raw `String` cannot express. `AttributeName<T>` makes the distinction explicit and prevents invalid names from entering the semantic model.

### Treat AttributeName as global semantic identity

Rejected because metadata already provides the semantic context. Requiring globally unique or instance-identity names would add complexity without improving the binding model.

### Let Data accept AttributeName directly

Rejected because arbitrary represented data would become coupled to semantic models it may never participate in.

### Let Attribute own a PropertyReference

Rejected because semantic attributes would become coupled to one physical representation layout.

### Define a textual PropertyReference in core

Rejected because textual naming is only one possible representation coordinate shape. Named coordinates have no privileged status over positions, paths, columns, members, or future coordinate systems.

### Let Metadata permanently own PropertyMapping

Rejected as the fundamental model because the same metadata may be associated with differently represented data. Mapping belongs to a particular binding relationship.
