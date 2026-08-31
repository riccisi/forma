# Forma

> **An object-oriented model for data.**

Forma is a Java library for representing, interpreting, composing, and validating data without requiring every data shape to become a Java POJO or DTO.

It starts from a simple observation:

> **A Java class is one possible representation of data, not the definition of data itself.**

In many Java applications, data is modeled by creating classes whose fields reproduce the structure of an external representation.

A JSON document becomes a DTO.  
A database row becomes an entity.  
An HTTP request becomes another DTO.  
A message may become yet another class.

The same information is repeatedly materialized into Java objects, exposed through getters or accessors, and then processed by external validators, mappers, converters, mergers, serializers, and other procedural utilities.

Forma explores a different approach.

Instead of copying data into passive objects, Forma represents the data source itself as an object and lets that object participate in interpretation.

```text
JSON        -> JsonData
JDBC row    -> JdbcData
Java object -> PojoData
Map         -> HashtableData
````

These are not necessarily intermediate representations waiting to become a DTO.

They are data.

---

## Why Forma?

Consider an external representation of a student:

```json
{
  "id": "S-42",
  "name": "Alice",
  "status": "ACTIVE",
  "description": "Imported from an external system"
}
```

A conventional Java model may reproduce the whole structure:

```java
public final class StudentDto {

    private String id;
    private String name;
    private String status;
    private String description;

    // getters, setters, constructors...
}
```

But what if application decisions only depend on:

```text
id
status
```

`description` is still part of the data, but it may never participate in any decision.

Nevertheless, the conventional approach usually promotes it into Java state anyway: a field, constructor argument, getter, mapper configuration, serialization rule, and possibly equivalent fields across several DTOs.

Forma does not require that.

```text
JsonData
 ├── id
 ├── name
 ├── status
 └── description
```

The complete information remains represented by `Data`, while only the portions that actually require semantic interpretation need to become model attributes.

> **The presence of information does not imply the need for an equivalent Java attribute.**

Or, more practically:

> **Interpret what the application needs; preserve the rest as data.**

---

## Core concepts

Forma separates representation from semantics through a small set of concepts:

```text
Data
  ↓
Property
  ↓
Attribute
  ↓
ModelAttribute
  ↓
Model
```

Each step introduces a stronger semantic commitment.

### Data

`Data` represents information.

It does not claim that the information is complete, valid, or associated with any particular business model.

Conceptually:

```java
public interface Data extends Iterable<Property> {
}
```

A `Data` may represent:

* a JSON document;
* a JDBC row;
* a Java object;
* a map;
* a positional record;
* a partial HTTP request;
* the result of combining other data.

A `Data` may also be intentionally incomplete.

For example:

```json
{
  "email": "new@example.com",
  "phone": "+390000000"
}
```

may be perfectly valid `Data` even though it is not a complete `Student`.

> **Data is transformable.**

---

### Property

A `Property` represents an interpretable portion of data.

Forma deliberately avoids reducing every property to:

```java
Object
```

or forcing every source through a universal representation such as:

```text
String
byte[]
InputStream
```

Doing so would throw away information already understood by the underlying technology.

A JSON parser already knows whether a value is a string, number, boolean, array, or object.

A JDBC driver may already expose `BigDecimal`, temporal values, booleans, binary values, and other useful representations.

Forma allows that knowledge to be preserved through an interpreter-style API.

Conceptually:

```java
public interface Property {

    <T> T describe(PropertyValue<T> value);
}
```

The property vocabulary remains representation-oriented.

Business concepts such as `Email`, `Money`, or `StudentId` belong to attributes, not properties.

This boundary also makes representation decorators possible:

```text
EncryptedProperty
    ↓
DecryptedProperty
    ↓
Attribute interpretation
```

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

---

### Attribute

An `Attribute<T>` describes a semantic coordinate of a model.

It determines how appropriate data is interpreted as `T` and which rules must hold for that value.

Examples may range from basic representations:

```text
TextAttribute
IntAttribute
DecimalAttribute
BooleanAttribute
```

to richer semantics:

```text
EmailAttribute
MoneyAttribute
StudentIdAttribute
```

and composable constraints:

```text
NonNullAttribute
PositiveAttribute
```

Conceptually:

```text
Property
    ↓
TextAttribute
    ↓
EmailAttribute
    ↓
NonNullAttribute
    ↓
ModelAttribute<Email>
```

Two attributes remain different even when they have compatible types.

```text
Student.email : Attribute<Email>
Teacher.email : Attribute<Email>
```

They share the semantic type `Email`, but they do not share identity.

This distinction may later enable type-safe projections between different models.

---

### Metadata

`Metadata` describes the semantic structure and invariants of a model.

It is not merely a passive list of fields.

Its main responsibility is to establish whether arbitrary `Data` can become a valid semantic model.

Conceptually:

```java
public interface Metadata extends Iterable<Attribute<?>> {

    Model bind(Data data);
}
```

Binding is a construction boundary:

```text
Data
 +
Metadata
    ↓
  bind
    ↓
Model
```

A successful binding produces a valid model.

An unsuccessful binding does not produce an invalid one.

> **Metadata binding is construction, not validation after construction.**

Therefore:

> **A Model either exists in a valid state, or it does not exist.**

---

### ModelAttribute

A `ModelAttribute<T>` represents an attribute that has been successfully interpreted and validated against concrete data.

Conceptually:

```java
public interface ModelAttribute<T> {

    AttributeName<T> name();

    T value();
}
```

It is evidence that a portion of represented information successfully satisfies a semantic attribute.

```text
Property + Attribute<T>
          ↓
       binding
          ↓
ModelAttribute<T>
```

---

### Model

A `Model` is data whose semantic structure and invariants have been successfully established by `Metadata`.

Conceptually:

```java
public interface Model {

    Metadata metadata();

    Data data();

    <T> T value(Attribute<T> attribute);
}
```

Unlike a DTO, a model does not necessarily reproduce its entire data representation as Java state.

It establishes a valid semantic interpretation over the underlying data.

For example:

```text
Data
 ├── id            -> ModelAttribute<StudentId>
 ├── status        -> ModelAttribute<StudentStatus>
 ├── description   -> preserved as Data
 └── sourceNotes   -> preserved as Data
```

Application behavior can reason about `id` and `status` without introducing Java fields for `description` and `sourceNotes` merely to avoid losing them.

> **Model is trustworthy.**

---

## Data composition

Since `Data` exists independently from semantic validity, it can be transformed before being bound to metadata.

For example, an application may receive a partial update over HTTP while the current state comes from a database:

```text
JdbcData
   +
JsonData
   ↓
MergedData
   ↓
StudentMetadata.bind(...)
   ↓
Student Model
```

Conceptually:

```java
Data candidate = new MergedData(
    current,
    update
);

Model student = students.bind(candidate);
```

Possible data compositions may include:

```text
MergedData
UnionData
IntersectedData
FilteredData
ProjectedData
```

These objects define transformations over information.

They do not need to know whether the resulting data represents a valid business model.

---

## Data is not necessarily materialized

Forma does not require a data source to be eagerly transformed into an equivalent Java object graph.

A `JsonData` may retain its JSON representation.

A `JdbcData` may retain the representation exposed by JDBC.

A `PojoData` may interpret a Java object when information is requested.

If an application never asks for `description`, Forma does not inherently require that value to become a Java `String` merely because it exists in the source.

This does not require every implementation to be lazy.

Implementations may materialize, cache, stream, or interpret on demand according to their own semantics.

The important property is:

> **Forma does not require representation to be materialized into equivalent Java state.**

---

## POJOs are still data

Forma does not reject Java objects.

It simply does not give them a privileged role.

A POJO is another possible representation:

```text
Student POJO
     ↓
  PojoData
```

This is different from a `PojoModel`.

A `PojoData` interprets an arbitrary Java object as data.

A `PojoModel` may additionally derive metadata from the Java class, annotations, reflection, or another description mechanism and bind that data automatically.

Conceptually:

```text
PojoData(object)
       +
PojoMetadata(object.getClass())
       ↓
      bind
       ↓
   PojoModel
```

The distinction remains:

> **A Java class may describe data, but data does not require a Java class describing it.**

---

## Representation and output are independent

Forma does not require `Data` or `Model` to implement a particular rendering or serialization API.

Input representation and output representation are independent concerns.

For example:

```text
JdbcData
   ↓
JsonPrintableData
   ↓
JSON
```

or:

```text
JsonData
   ↓
XmlPrintableData
   ↓
XML
```

A future printing API can therefore be implemented through composition rather than making every `Data` or `Model` directly responsible for every possible media type.

---

## Design principles

Forma is guided by a few principles.

> **A Java class is one possible representation of data, not the definition of data itself.**

> **The presence of information does not imply the need for an equivalent Java attribute.**

> **Interpret what the application needs; preserve the rest as data.**

> **Data is transformable.**

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

> **Metadata binding is construction, not validation after construction.**

> **A Model either exists in a valid state, or it does not exist.**

> **Model is trustworthy.**

---

## What Forma is not

Forma is not:

* an ORM;
* a JSON library;
* a serialization framework;
* a bean-validation framework;
* a DTO generator;
* a mapping framework;
* a replacement for JDBC;
* a domain modeling framework.

Forma provides an object model that those technologies can be represented through or integrated with.

It intentionally remains independent from HTTP, persistence, messaging, CQRS, Event Sourcing, and application frameworks.

---

## Project status

Forma is currently in its early design and development stage.

The semantic model is being defined first. Concrete APIs and integrations will evolve as implementations such as JSON, POJO, map-based, and composed data are developed and used to validate the model.

In particular, some API details are intentionally still open, including:

* the final `Property` interpreter vocabulary;
* binding failure and validation reporting;
* the exact role of typed `AttributeName<T>`;
* property addressing;
* typed model projections;
* canonical model representation;
* printing and serialization APIs.

The goal is to let concrete use cases shape these APIs without weakening the core semantic model.

---

## Package

The root Java package is:

```text
it.riccisi.forma
```

---

## Inspiration

Forma is inspired in part by the **Parsing Objects** approach: objects should encapsulate the representation they work with instead of eagerly parsing it into passive data structures that are subsequently manipulated by procedural code.

Forma generalizes that idea beyond parsing and beyond any specific domain object, treating representation, composition, semantic interpretation, and model validity as separate object responsibilities.

---

## License

To be defined.

```
