# ADR-001 — Treat Data as Objects and Separate Representation from Semantic Models

## Status

Proposed

## Context

Java applications commonly represent data by defining classes whose fields reproduce the structure of that data. This makes the Java class itself coincide with the notion of data and tends to turn objects into passive state containers manipulated by validators, mappers, serializers, converters, and similar external procedures.

The same logical information is often duplicated across technical boundaries through request DTOs, domain DTOs, persistence entities, and message DTOs even when application behavior depends on only a small portion of that information.

Forma rejects the requirement that every represented property must become equivalent Java state.

> **The presence of information does not imply the need for an equivalent Java attribute.**

> **Data should be represented without requiring its entire structure to become Java object state.**

A Java POJO is therefore one possible representation of data, not the definition of data itself.

## Decision drivers

Forma should:

* model data through objects rather than passive state containers;
* preserve information that is not currently decisionally relevant;
* support partial and intermediate data;
* allow different physical representations of the same information;
* separate representation coordinates, representation values, and semantic meaning;
* avoid representation-specific casts or type inspection in semantic attributes;
* make valid semantic objects valid by construction;
* remain independent from persistence, messaging, CQRS, event sourcing, HTTP, and other application frameworks.

## Decision

Forma introduces the following core abstractions:

```text
Data
PropertyReference
Property
PropertyValue
Attribute<T>
AttributeName<T>
PropertyMapping
Metadata
ModelAttribute<T>
Model
```

They separate three concerns:

```text
representation coordinates
representation values
semantic meaning
```

### Data

`Data` represents information without claiming conformance to a particular business structure.

```java
public interface Data extends Iterable<Property> {

    Property property(PropertyReference reference);
}
```

A `Data` deliberately does not accept `AttributeName` as a lookup coordinate. It must be able to exist independently from any `Model` or `Metadata` that may later be associated with it and therefore speaks only in representation coordinates.

Keeping `Data` iterable over `Property` is important because properties remain first-class representation objects over which compositions such as merging, filtering, and projection can operate.

A `Data` implementation is not required to eagerly materialize its representation into an equivalent Java object graph. Concrete implementations remain free to cache, materialize, stream, or lazily interpret information according to their semantics.

> **Data is transformable.**

### PropertyReference

`PropertyReference` is the only coordinate abstraction required by Forma core:

```java
public interface PropertyReference {
}
```

The contract is deliberately opaque. Forma core does not classify representation coordinates as names, positions, paths, columns, members, or any other particular shape.

Concrete representations are free to define the coordinates that naturally describe them:

```java
record JsonField(String name) implements PropertyReference {}
record JdbcColumn(String name) implements PropertyReference {}
record Position(int index) implements PropertyReference {}
record PropertyPath(List<String> segments) implements PropertyReference {}
```

These examples are representation concepts, not core semantic concepts.

A named coordinate has no privileged status over a positional or path-based coordinate. For this reason Forma does not introduce a core `PropertyName` specialization. Such a type may exist inside a representation-specific module if that representation benefits from it.

This establishes:

> **Forma core knows that data has coordinates, not what shape those coordinates have.**

### Property

A `Property` is an individually interpretable portion of `Data`:

```java
public interface Property {

    PropertyValue value();
}
```

Forma deliberately rejects `Object value()` and does not force every value through `String`, `byte[]`, or another universal carrier.

### PropertyValue

`PropertyValue` is the small common vocabulary through which primitive represented values can be interpreted.

The initial contract is deliberately small:

```java
public interface PropertyValue {

    Text asText();

    Number asNumber();
}
```

Concrete value objects such as `TextValue` and `NumberValue` own representation-level conversion rules. This centralizes primitive conversion semantics instead of repeating them in every JSON, JDBC, POJO, Map, or other concrete property implementation.

`PropertyValue` remains representation-level and must not contain business concepts such as `Email`, `Money`, or `StudentId`; those belong to `Attribute`.

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

### Attribute

`Attribute<T>` describes a semantic coordinate of a model:

```java
public interface Attribute<T> {

    AttributeName<T> name();

    ModelAttribute<T> bind(Property property);
}
```

An attribute obtains the primitive interpretation it needs from `PropertyValue` and establishes semantic meaning and validity. It does not know how its semantic identity is associated with a concrete representation coordinate.

### AttributeName<T>

`AttributeName<T>` identifies a semantic coordinate. It is deliberately distinct from `PropertyReference`.

Two attributes can expose the same Java type or similar textual labels while remaining semantically distinct. Conversely, one semantic attribute may be represented by differently shaped coordinates in different data sources.

### PropertyMapping

`PropertyMapping` belongs to the binding relationship between semantic metadata and represented data:

```java
public interface PropertyMapping {

    PropertyReference property(AttributeName<?> attribute);
}
```

It belongs neither intrinsically to `Data` nor to `Attribute` or `Metadata` as owned state. It is supplied when a particular semantic model is associated with a particular representation.

The relationship is:

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

The mapping may be explicit or may embody a representation convention. Crucially, the contract does not require textual name equality and does not require the source coordinate to be textual at all.

The same `Metadata` can therefore bind differently represented `Data`, and the same `Data` can participate in different semantic bindings without itself acquiring semantic knowledge.

> **Data knows representation. Metadata knows semantics. PropertyMapping relates their coordinates.**

> **PropertyMapping belongs to the binding relationship, not to Data and not to Metadata.**

### Metadata

`Metadata` defines semantic structure and invariants and receives the mapping required for a particular binding:

```java
public interface Metadata extends Iterable<Attribute<?>> {

    Model bind(Data data, PropertyMapping mapping);
}
```

Binding conceptually performs:

```text
AttributeName
     ↓
PropertyMapping
     ↓
PropertyReference
     ↓
Data.property(...)
     ↓
Property
     ↓
PropertyValue
     ↓
Attribute.bind(...)
     ↓
ModelAttribute
```

Addressing and interpretation remain separate concerns:

```text
addressing:
AttributeName -> PropertyReference

interpretation:
Property -> PropertyValue -> semantic value
```

A semantic attribute can consequently bind to a named field, a positional value, a nested path, a JDBC column, a POJO member, or another coordinate without changing the semantic model or the Forma core API.

### Binding is construction

`Metadata.bind(Data, PropertyMapping)` establishes semantic validity while constructing the model.

> **Metadata binding is construction, not validation after construction.**

> **A Model either exists in a valid state, or it does not exist.**

The exact representation of binding failures remains a separate API decision.

### ModelAttribute

`ModelAttribute<T>` represents successful interpretation and validation of represented information against an `Attribute<T>`:

```java
public interface ModelAttribute<T> {

    AttributeName<T> name();

    T value();
}
```

### Model

A `Model` represents `Data` that has successfully acquired semantic structure through `Metadata`:

```java
public interface Model {

    Metadata metadata();

    Data data();

    <T> T value(AttributeName<T> name);
}
```

A Model establishes a valid semantic interpretation over data rather than reproducing the entire data shape as Java state.

> **Model is trustworthy.**

Retaining access to the underlying `Data` allows information that is not currently decisionally relevant to survive without being promoted into `ModelAttribute`s.

### Representation-specific models

Objects such as `JsonData`, `JdbcData`, `PojoData`, and positional data implementations may define their own `PropertyReference` implementations. Forma core remains unaware of those coordinate shapes.

Likewise, output rendering is orthogonal to both source representation and semantic validity and should be introduced through composition rather than by requiring `Data` or `Model` to implement output formats directly.

## Conceptual model

```text
                   REPRESENTATION
                        │
                        ▼
                      Data
                        │
               PropertyReference
                        │
                        ▼
                    Property
                        │
                        ▼
                  PropertyValue
                        │
                        │ interpreted by
                        ▼
                    Attribute
                        │
                        │ defined by
                        ▼
                     Metadata
                        │
                        │ bind using
                        ▼
                 PropertyMapping
                        │
                        ▼
                      Model
                        │
                  ModelAttribute
                        │
                        ▼
                     SEMANTICS
```

The binding coordinate relationship can be summarized independently as:

```text
semantic coordinate                         representation coordinate

AttributeName ───── PropertyMapping ─────> PropertyReference ─────> Data
```

## Fundamental principles

> **A Java class is one possible representation of data, not the definition of data itself.**

> **The presence of information does not imply the need for an equivalent Java attribute.**

> **Data should be represented without requiring its entire structure to become Java object state.**

> **Data knows representation. Metadata knows semantics. PropertyMapping relates their coordinates.**

> **Forma core knows that data has coordinates, not what shape those coordinates have.**

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

> **Metadata binding is construction, not validation after construction.**

> **A Model either exists in a valid state, or it does not exist.**

> **Model is trustworthy.**

## Consequences

Forma avoids privileging named representations over positional, hierarchical, column-oriented, or other coordinate systems.

Representation-specific coordinate types can evolve independently without expanding the Forma core vocabulary.

Semantic attributes remain independent from physical representation coordinates.

A `PropertyMapping` can associate the same semantic metadata with radically different source layouts.

Different mappings can associate the same represented `Data` with different semantic structures without modifying the `Data` itself.

The abstraction requires callers to provide a mapping when binding. This is deliberate: the association between semantics and representation is contextual information and should not be hidden inside either side.

## Alternatives considered

### Data directly accepts AttributeName

Rejected because it couples arbitrary represented data to a semantic model it may never participate in.

### Attribute owns its representation coordinate

Rejected because semantic attributes would become coupled to JSON names, JDBC columns, POJO members, positional indexes, or other physical layouts.

### Metadata permanently owns the mapping

Rejected as the fundamental model because the same metadata may be applied to differently represented data. Mapping belongs to a particular binding relationship.

### Core PropertyName specialization

Rejected. A name is only one possible representation coordinate and has no behavior or invariant required by Forma core beyond `PropertyReference`. Keeping `PropertyName` in core would privilege named representations and add a classification that binding does not need.

### Property exposes Object

Rejected because it shifts representation interpretation and casts into clients.

### Closed PropertyValue interpreter

Rejected after experimentation. A visitor containing `text`, `number`, `boolean`, and other methods forces specialized semantic attributes to implement value kinds they cannot consume. Attributes are naturally partial interpreters, while a closed visitor models a total interpretation over all variants.

### Capability interfaces plus instanceof

Rejected after experimentation. Interfaces such as `TextProperty` make source capabilities explicit but require runtime type inspection or additional witness objects when heterogeneous metadata binds generic properties.

## Scope

Forma is an independent library. Its semantic model must not depend on Kern, Weave, CQRS, Event Sourcing, HTTP, JDBC, JSON, persistence frameworks, or messaging frameworks.

Representation-specific integrations may depend on external technologies in separate artifacts.

The library is named **Forma**, uses root package `it.riccisi.forma`, and is positioned as:

> **Forma — an object-oriented model for data.**

## Open questions

The following details remain deliberately unresolved:

* the exact failure model of `Metadata.bind`;
* fail-fast versus accumulated validation violations;
* missing-property semantics;
* the exact role and runtime identity semantics of `AttributeName<T>`;
* whether `Model` lookup should expose additional forms;
* whether binding remains lazy where possible or evaluates every required attribute;
* canonical model representation and equality;
* business identity based on combinations of attributes;
* typed projections between models;
* concrete `Data` transformation algebra;
* media-independent printing and serialization APIs;
* additional `PropertyValue` primitive interpretations when justified by concrete representations.
