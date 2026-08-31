
# ADR-001 — Treat Data as Objects and Separate Representation from Semantic Models

## Status

Proposed

## Context

Java applications commonly represent data by defining classes whose fields reproduce the structure of that data.

A typical application introduces POJOs, DTOs, records, persistence entities, request objects, response objects, or message payload classes such as:

```java
final class StudentDto {

    private String id;
    private String name;
    private String email;
    private String description;
    private Integer age;

    // getters/setters
}
```

This makes the Java class itself coincide with the notion of data:

```text
data = instance of a class reproducing its structure
```

Once the state has been exposed through getters, setters, record accessors, or similar APIs, operations over that data tend to become procedural and are implemented by external components:

```text
validator.validate(student)
mapper.map(student)
serializer.serialize(student)
merger.merge(student, patch)
converter.convert(student)
```

The object becomes primarily a passive state container while other objects extract, inspect, transform, and validate its contents.

This approach also frequently duplicates the same logical information across technical boundaries:

```text
HTTP request
     ↓
Request DTO
     ↓ mapping
Domain DTO
     ↓ mapping
Persistence Entity
     ↓ mapping
Message DTO
```

Each class reproduces a shape of the data even when very little behavior actually depends on most of that shape.

## Not all data is decisionally relevant

Mapping data into Java classes also tends to promote **every property of the source representation into explicit Java state**, regardless of whether the application actually uses that property to make decisions.

For example, an external Student representation may contain:

```json
{
  "id": "S-42",
  "name": "Alice",
  "status": "ACTIVE",
  "description": "Student imported from external system"
}
```

Application behavior may depend on:

```text
id
status
```

while `description` may only need to survive transport, persistence, transformation, or later rendering.

Nevertheless, the conventional approach usually introduces:

```java
private String description;
```

together with its constructor parameter, accessor, mapping configuration, copying logic, serialization annotations, and possibly equivalent properties in several DTOs.

The existence of data in the source therefore creates Java structure even when that structure has no role in application decisions.

Forma rejects this requirement.

A `Data` object encapsulates the complete represented information, but individual portions of that information only need to be interpreted when explicitly requested.

For example:

```text
JsonData
 ├─ id
 ├─ name
 ├─ status
 └─ description
```

does not require four corresponding Java fields or four eagerly materialized Java values.

`description` can remain encapsulated in the underlying representation until some consumer actually asks for the corresponding property.

Therefore:

> **The presence of information does not imply the need for an equivalent Java attribute.**

And:

> **Data should be represented without requiring its entire structure to become Java object state.**

This also allows Forma to preserve information which is currently irrelevant to business decisions without discarding it or forcing it into the application's object model.

## Object-oriented data representation

Forma adopts a different model.

A datum is not defined by a Java class containing equivalent fields.

Instead, a `Data` object represents information in whatever source representation currently owns it and encapsulates how that representation can be interpreted.

Examples include:

```text
JSON        → JsonData
JDBC row    → JdbcData
Java object → PojoData
Map         → HashtableData
CSV/record  → PositionalData
```

These objects are not necessarily temporary stages on the way toward a DTO.

They are themselves object-oriented representations of data.

A Java POJO is therefore considered:

> **one possible representation of data, not the definition of data itself.**

This idea is inspired by parsing objects: rather than parsing a representation into a passive object and subsequently working on its exposed state, the object encapsulating the representation remains responsible for allowing that representation to be interpreted.

Forma generalizes this approach beyond a specific domain class or input format.

## Decision drivers

Forma should:

* model data through objects rather than passive state containers;
* avoid requiring a Java class whose fields reproduce every data shape;
* avoid requiring every source property to become eagerly materialized Java state;
* allow unused information to remain encapsulated until explicitly requested;
* preserve information even when it is not currently decisionally relevant;
* support partial and intermediate data;
* allow different physical representations of the same information;
* allow data to be transformed and composed before assigning business meaning;
* preserve capabilities already provided by underlying parsers and data sources;
* separate representation concerns from semantic structure and validation;
* make valid semantic objects valid by construction;
* enable object composition instead of procedural mapping pipelines;
* remain independent from persistence, messaging, CQRS, event sourcing, HTTP, and other application frameworks.

## Decision

Forma introduces separate abstractions for:

```text
Data
Property
Attribute<T>
AttributeName<T>
Metadata
ModelAttribute<T>
Model
```

They represent progressively stronger semantic commitments.

```text
Data
  ↓
Property
  ↓
Attribute interpretation
  ↓
ModelAttribute
  ↓
Model
```

### Data

`Data` represents information without claiming conformance to a particular business structure.

Conceptually:

```java
public interface Data extends Iterable<Property> {
}
```

A `Data` may be:

* complete;
* partial;
* projected;
* merged;
* filtered;
* dynamically backed by another source;
* unrelated to any known business model.

A JSON payload such as:

```json
{
  "email": "new@example.com",
  "phone": "+390000000"
}
```

is therefore valid `Data` even if it is not, by itself, a valid `Student`.

`Data` represents information, not semantic validity.

> **Data is transformable.**

### Data does not require eager materialization

A `Data` implementation is not required to extract every property from its underlying source when constructed.

For example:

```java
new JsonData(json)
```

may retain the JSON representation itself.

If no consumer ever asks for:

```text
description
```

there is no inherent requirement for Forma to convert that value into a Java `String`.

Likewise, `JdbcData` may preserve the capabilities of the JDBC representation and `PojoData` may interpret its represented object only when necessary.

This is not defined as mandatory laziness.

It is a stronger architectural property:

> **Forma does not require representation to be materialized into an equivalent Java object graph.**

Concrete implementations remain free to cache, materialize, stream, or lazily interpret information according to their own semantics.

### Data may be structurally immutable while representing mutable information

A `Data` object's identity and configuration may be immutable while the source it represents changes independently.

For example, a `HashtableData` may retain a reference to a mutable table.

Forma does not equate structural object immutability with temporal stability of the represented source.

Implementations that require snapshot semantics must establish those semantics explicitly.

### Data composition

Because `Data` does not imply semantic validity, it may be transformed before binding.

Examples may include:

```text
MergedData
UnionData
IntersectedData
ProjectedData
FilteredData
```

For example:

```java
Data candidate = new MergedData(
    new JdbcData(row),
    new JsonData(request)
);
```

`MergedData` defines merge semantics only.

It does not decide whether the resulting information constitutes a valid Student.

That responsibility belongs to `Metadata`.

### Property

A `Property` is an individually interpretable portion of `Data`.

Forma must not force all representations through:

```java
Object value();
```

or through a single universal representation such as:

```java
String
byte[]
InputStream
```

Doing so would discard knowledge already available in the underlying source.

A JSON parser may already know that something is:

```text
string
number
boolean
array
object
```

while JDBC may already expose:

```text
BigDecimal
temporal values
booleans
binary data
```

Re-encoding these values into a universal representation only to parse them again would move interpretation back into procedural client code.

Instead, `Property` uses an interpreter:

```java
public interface Property {

    <T> T describe(PropertyValue<T> value);
}
```

The exact vocabulary of `PropertyValue` is intentionally deferred.

Its responsibility is to expose a **small representation-level algebra**, not business semantics.

Conceptually it may distinguish things such as:

```text
text
number
boolean
array
nested data
```

but must not introduce concepts such as:

```text
Email
Money
StudentId
```

Those belong to `Attribute`.

### Property transformations

Because `Property` encapsulates representation, decorators can transform representation without involving business semantics.

Possible examples include:

```text
DecryptedProperty
DecodedProperty
DecompressedProperty
```

Conceptually:

```text
physical representation
        ↓
Property decorator
        ↓
logical representation
        ↓
Attribute
        ↓
semantic value
```

This establishes:

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

### Attribute

`Attribute<T>` describes a semantic coordinate of a model.

An attribute determines:

* what semantic value is expected;
* how an appropriate `Property` is interpreted;
* which rules must hold for that value.

Conceptually:

```java
public interface Attribute<T> {

    AttributeName<T> name();

    ModelAttribute<T> bind(Property property);
}
```

The exact failure API is intentionally deferred.

Primitive interpretations may include:

```text
TextAttribute
IntAttribute
DecimalAttribute
BooleanAttribute
```

while composition may add stronger semantics:

```text
NonNullAttribute
PositiveAttribute
EmailAttribute
MoneyAttribute
```

For example:

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

Interpretation and validation therefore remain object compositions rather than a sequence of external procedures.

### Attribute identity

Two attributes do not have the same identity merely because they expose the same Java type or textual name.

For example:

```text
Student.email
Teacher.email
```

are distinct attributes even if both are:

```java
Attribute<Email>
```

Likewise:

```text
Student.name
Course.name
```

remain distinct even if both produce `String`.

The semantic type may make attributes compatible for some transformations or projections without making their identities equal.

### AttributeName<T>

Attribute names may carry their result type:

```java
AttributeName<Email> email;
AttributeName<Integer> age;
```

allowing typed APIs such as:

```java
Email email = model.value(emailName);
```

without casts in application code.

The generic type does not by itself provide runtime safety because of Java type erasure.

The invariant must instead be established while constructing the `Model`: an `AttributeName<T>` may only refer to a `ModelAttribute<T>` created through a successful metadata binding.

The final public role of `AttributeName<T>` remains an API design decision.

### Metadata

`Metadata` defines the semantic structure and invariants of a model.

Conceptually:

```java
public interface Metadata extends Iterable<Attribute<?>> {

    Model bind(Data data);
}
```

`Metadata` does not merely describe fields.

It establishes whether arbitrary `Data` may be interpreted as a specific valid model.

```text
Data
  +
Metadata
    ↓ bind
Model
```

Property lookup does not necessarily imply textual name equality.

An attribute may obtain its property from:

```text
JSON field
Map key
JDBC column
POJO property
positional field
```

For example:

```text
property "e_mail_address"
       ↓
Student.email
```

or:

```text
position 7
       ↓
Student.birthDate
```

may both be valid associations.

Representation coordinates and semantic attribute identities are therefore separate concepts.

### Binding is construction

`Metadata.bind(Data)` establishes semantic validity while constructing the model.

A successful result must never require:

```java
model.isValid()
```

or:

```java
validator.validate(model)
```

before use.

A `Model` which exists is already valid according to its metadata.

> **Metadata binding is construction, not validation after construction.**

Therefore:

> **A Model either exists in a valid state, or it does not exist.**

The representation of binding failures and whether multiple violations are accumulated are intentionally deferred.

### ModelAttribute

`ModelAttribute<T>` represents the successful binding of an `Attribute<T>` against concrete data.

Conceptually:

```java
public interface ModelAttribute<T> {

    AttributeName<T> name();

    T value();
}
```

Its value has already been interpreted and validated.

Conceptually:

```text
Property
    +
Attribute<T>
      ↓
ModelAttribute<T>
```

A `ModelAttribute` is therefore evidence that a concrete portion of represented data satisfies a semantic attribute.

### Model

A `Model` represents `Data` that has successfully acquired a semantic structure through `Metadata`.

Conceptually:

```java
public interface Model {

    Metadata metadata();

    Data data();

    <T> T value(Attribute<T> attribute);
}
```

The exact lookup API may ultimately use `Attribute<T>`, `AttributeName<T>`, or both.

A `Model` differs fundamentally from a DTO.

A DTO usually reproduces a data shape as Java state.

A Model establishes a **valid semantic interpretation** over data.

The underlying information does not necessarily have to be copied into equivalent Java fields.

> **Model is trustworthy.**

### Access to underlying Data

A `Model` may retain access to the `Data` from which it was constructed.

This does not weaken the model invariant.

`data()` means:

> this is the representation from which this valid semantic interpretation was established.

This enables additional transformations without forcing all information into `ModelAttribute`s.

It is especially useful when the original data contains properties which are not part of the decisionally relevant model.

For example:

```text
Data
 ├─ id            → ModelAttribute
 ├─ status        → ModelAttribute
 ├─ description   → retained as Data
 └─ sourceNotes   → retained as Data
```

The `Model` can reason about:

```text
id
status
```

without requiring `description` or `sourceNotes` to become explicit Java state merely to preserve them.

This is a deliberate feature of Forma.

### PojoData and PojoModel

`PojoData` and `PojoModel` represent different concepts.

`PojoData` treats a Java object as a source representation:

```text
POJO
 ↓
PojoData
```

It does not inherently claim that the Java class defines a semantic model.

`PojoModel` may instead derive metadata from the POJO class, annotations, reflection, or another class-description mechanism.

Conceptually:

```text
PojoData(object)
        +
PojoMetadata(object.class)
        ↓
       bind
        ↓
    PojoModel
```

This preserves the principle that a Java class is one possible representation rather than the universal definition of data.

### Cross-model compatibility

Two distinct attributes may share a semantic type:

```text
Student.email : Attribute<Email>
Teacher.email : Attribute<Email>
Contact.email : Attribute<Email>
```

They remain separate attributes but may participate in type-safe mappings or projections.

This may enable future APIs where compatible portions of different models are projected without relying on property-name coincidence.

Semantic value objects such as:

```text
Email
Money
StudentId
CountryCode
```

can make such compatibility stronger than using broad Java types such as `String`.

The exact projection API is outside the scope of this ADR.

### Rendering is orthogonal

Neither `Data` nor `Model` is required to implement a printing or serialization capability.

Output representation is independent from input representation.

For example, a `JdbcData` may legitimately be rendered as JSON.

Rendering should therefore be introduced through composition:

```text
JsonPrintableData(Data)
JsonPrintableModel(Model)
XmlPrintableData(Data)
```

rather than forcing:

```java
Data extends Printable
```

or:

```java
Model extends Printable
```

This keeps represented media and output media independent.

## Conceptual model

The resulting architecture is:

```text
                  REPRESENTATION
                       │
                       ▼
                     Data
                       │
             ┌─────────┼─────────┐
             │         │         │
         JsonData   JdbcData  PojoData
             │
             │ transformations
             ▼
         MergedData
         FilteredData
         ProjectedData
             │
             ▼
                   Properties
                       │
                       │ interpreted by
                       ▼
                   Attributes
                       │
                       │ defined by
                       ▼
                    Metadata
                       │
                       │ bind
                       ▼
                     Model
                       │
                 ModelAttributes
                       │
                       ▼
                    SEMANTICS
```

Another way to express the progression is:

```text
Data
    arbitrary information

Property
    interpretable representation

Attribute
    semantic expectation

ModelAttribute
    successfully interpreted semantic value

Metadata
    model structure and invariants

Model
    valid semantic interpretation of Data
```

## Fundamental principles

Forma adopts the following principles:

> **A Java class is one possible representation of data, not the definition of data itself.**

> **The presence of information does not imply the need for an equivalent Java attribute.**

> **Data should be represented without requiring its entire structure to become Java object state.**

> **Data is transformable.**

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

> **Metadata binding is construction, not validation after construction.**

> **A Model either exists in a valid state, or it does not exist.**

> **Model is trustworthy.**

## Consequences

Forma avoids requiring a dedicated Java class for every shape of data entering or leaving an application.

Information irrelevant to current decisions can remain encapsulated in its source representation without becoming unnecessary Java fields.

Data may retain properties not currently represented as model attributes, avoiding accidental information loss while also avoiding artificial expansion of the semantic object model.

Different representations can participate in the same semantic model.

Partial data becomes a first-class concept rather than an invalid DTO.

Merge, projection, filtering, intersection, encryption-related transformations, and similar operations can occur at the representation layer before semantic binding.

Validation becomes part of object construction.

Underlying parser and source capabilities can be retained instead of normalized prematurely into strings, bytes, or generic objects.

Application code can depend on semantic attributes rather than physical representation coordinates.

The abstraction introduces an interpreter boundary for properties and therefore requires more implementation machinery than passive POJOs. This complexity is accepted because it moves representation-specific procedural logic behind object boundaries.

## Alternatives considered

### Represent every data shape with a POJO or DTO

Rejected as Forma's fundamental model.

This duplicates source structure in Java, often materializes properties which have no behavioral relevance, and encourages external procedural manipulation through accessors and utility classes.

POJOs remain supported as one representation through `PojoData`.

### Every Data has Metadata

Rejected.

Partial and intermediate information may not yet satisfy a complete semantic structure.

`Data` exists independently; successful metadata binding produces a `Model`.

### Data directly exposes semantic attributes

For example:

```java
<T> T value(Attribute<T> attribute);
```

Rejected as the fundamental `Data` API.

Raw information should not need to claim semantic interpretation before metadata binding.

### Property exposes Object

Rejected.

It shifts type interpretation and casts to clients.

### Property has one universal byte or textual representation

Rejected.

It throws away information already available from source-specific parsers and frequently introduces unnecessary serialization and parsing.

### Model may exist in an invalid state

Rejected.

Validity is a construction invariant.

### Data and Model implement output formats directly

Rejected.

Rendering is an orthogonal interpretation that should be composed independently.

## Scope

Forma is an independent library.

Its semantic model must not depend on:

```text
Kern
Weave
CQRS
Event Sourcing
HTTP
JDBC
JSON
persistence frameworks
messaging frameworks
```

Representation-specific integrations may depend on external technologies in separate artifacts.

Other projects may consume Forma, but Forma must remain unaware of those consumers.

The library is named:

# **Forma**

with the intended root package:

```text
it.riccisi.forma
```

and the positioning:

> **Forma — an object-oriented model for data.**

## Open questions

The following details remain deliberately unresolved:

* the exact `PropertyValue` interpreter vocabulary;
* how properties are addressed inside a `Data`;
* whether `Property` itself owns a name;
* the exact failure model of `Metadata.bind`;
* fail-fast versus accumulated validation violations;
* the definitive contract of `Attribute<T>`;
* the exact role of `AttributeName<T>`;
* whether `Model` lookup uses `Attribute<T>`, `AttributeName<T>`, or both;
* how `Metadata` associates attributes with physical properties;
* whether binding remains lazy where possible or always evaluates every required attribute;
* whether `ModelAttribute` stores its semantic value or may itself remain an interpreted view;
* canonical model representation and equality;
* business identity based on combinations of attributes;
* typed projections between models;
* concrete `Data` transformation algebra;
* media-independent printing and serialization APIs.

---

Credo che questa versione esprima molto meglio **perché esiste Forma**. In particolare, il passaggio sugli attributi non decisionally relevant secondo me non è affatto secondario: distingue Forma non solo da un sistema di schema/validation, ma anche dall'idea classica secondo cui per “avere” un dato in Java bisogna necessariamente **trasformare tutta la sua struttura in stato Java**.

Anzi, terrei come principio fondante proprio questa coppia:

> **The presence of information does not imply the need for an equivalent Java attribute.**
> **Interpret what the application needs; preserve the rest as data.**

La seconda frase non l'ho ancora resa normativa nell'ADR, ma potrebbe tranquillamente diventare una sorta di motto tecnico di Forma.
