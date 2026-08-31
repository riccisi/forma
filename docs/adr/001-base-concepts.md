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

The same logical information is then often duplicated across technical boundaries:

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

Mapping data into Java classes tends to promote every property of the source representation into explicit Java state, regardless of whether the application actually uses that property to make decisions.

For example:

```json
{
  "id": "S-42",
  "name": "Alice",
  "status": "ACTIVE",
  "description": "Student imported from external system"
}
```

Application behavior may depend only on:

```text
id
status
```

while `description` may only need to survive transport, persistence, transformation, or later rendering.

Nevertheless, the conventional approach usually introduces a Java field for `description` together with constructor parameters, accessors, mapping configuration, copying logic, serialization annotations, and equivalent fields in other DTOs.

Forma rejects this requirement.

A `Data` object encapsulates represented information, but individual portions of that information only need to be interpreted when explicitly requested.

Therefore:

> **The presence of information does not imply the need for an equivalent Java attribute.**

And:

> **Data should be represented without requiring its entire structure to become Java object state.**

## Object-oriented data representation

Forma treats a datum as an object representing information in whatever source representation currently owns it.

Examples include:

```text
JSON        → JsonData
JDBC row    → JdbcData
Java object → PojoData
Map         → HashtableData
CSV/record  → PositionalData
```

These objects are not necessarily temporary stages on the way toward a DTO. They are themselves object-oriented representations of data.

A Java POJO is therefore:

> **one possible representation of data, not the definition of data itself.**

Forma is inspired by parsing objects: rather than parsing a representation into a passive object and subsequently working on exposed state, the object encapsulating the representation remains responsible for representing that information.

## Decision drivers

Forma should:

* model data through objects rather than passive state containers;
* avoid requiring a Java class whose fields reproduce every data shape;
* avoid requiring every source property to become eagerly materialized Java state;
* preserve information even when it is not currently decisionally relevant;
* support partial and intermediate data;
* allow different physical representations of the same information;
* allow data to be transformed and composed before assigning business meaning;
* separate representation concerns from semantic structure and validation;
* avoid representation-specific casts or type inspection in semantic attributes;
* make valid semantic objects valid by construction;
* enable object composition instead of procedural mapping pipelines;
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

They separate three concerns that must not collapse into one another:

```text
representation coordinates
representation values
semantic meaning
```

### Data

`Data` represents information without claiming conformance to a particular business structure.

Conceptually:

```java
public interface Data extends Iterable<Property> {

    Property property(PropertyReference reference);
}
```

A `Data` may be complete, partial, projected, merged, filtered, dynamically backed by another source, or unrelated to any known business model.

`Data` deliberately does not accept `AttributeName` as a lookup coordinate. A data representation must be able to exist independently from any `Model` or `Metadata` that may later be associated with it.

It therefore speaks only in representation coordinates.

> **Data is transformable.**

### Data does not require eager materialization

A `Data` implementation is not required to extract every property from its underlying source when constructed.

For example:

```java
new JsonData(json)
```

may retain the JSON representation itself. If no consumer ever asks for `description`, there is no inherent requirement for Forma to convert that value into a Java `String`.

This is not mandatory laziness. It is the stronger architectural property that:

> **Forma does not require representation to be materialized into an equivalent Java object graph.**

Concrete implementations remain free to cache, materialize, stream, or lazily interpret information according to their own semantics.

### Data may be structurally immutable while representing mutable information

A `Data` object's identity and configuration may be immutable while the source it represents changes independently.

Forma does not equate structural object immutability with temporal stability of the represented source. Implementations requiring snapshot semantics must establish those semantics explicitly.

### Data composition

Because `Data` does not imply semantic validity, it may be transformed before binding.

Examples may include:

```text
MergedData
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

`MergedData` defines merge semantics only. It does not decide whether the resulting information constitutes a valid Student. That responsibility belongs to `Metadata`.

Keeping `Data` iterable over `Property` is important because properties remain first-class representation objects over which compositions can operate.

### PropertyReference

`PropertyReference` is a coordinate understood by a concrete `Data` representation.

It is intentionally separate from `AttributeName`.

Possible representations may use:

```text
PropertyName
position
path
JDBC column
JSON pointer
POJO member
```

A named coordinate may be represented by:

```java
public interface PropertyName extends PropertyReference {
}
```

but Forma must not assume that every property is naturally name-based.

### Property

A `Property` is an individually interpretable portion of `Data`.

It exposes a representation-neutral value object:

```java
public interface Property {

    PropertyValue value();
}
```

Forma deliberately rejects a raw universal carrier such as:

```java
Object value();
```

because that would move casts and representation interpretation into clients.

It also rejects forcing every value through a single `String`, `byte[]`, or `InputStream`, because that may discard information already available in the underlying representation and cause unnecessary re-encoding and parsing.

### PropertyValue

`PropertyValue` is the small common vocabulary through which primitive represented values can be interpreted.

Conceptually, the initial contract is deliberately small:

```java
public interface PropertyValue {

    Text asText();

    Number asNumber();
}
```

Additional fundamental interpretations such as booleans, nested data, collections, binary values, or temporal values must be introduced only when concrete representations demonstrate that they belong in the common model.

`PropertyValue` is not an interpreter or closed visitor. This avoids requiring every semantic attribute to implement methods for value kinds that it cannot consume.

Instead, concrete value objects own representation-level conversion rules:

```text
TextValue
NumberValue
```

For example:

```text
TextValue("42").asText()   -> "42"
TextValue("42").asNumber() -> 42
NumberValue(42).asNumber()  -> 42
NumberValue(42).asText()    -> "42"
```

A conversion is valid when the represented information admits that interpretation. A textual value containing a valid number may therefore legitimately provide `asNumber()`. A textual value that cannot be interpreted numerically fails at that representation boundary.

This design centralizes primitive conversion logic in reusable value objects instead of repeating it in every `JsonProperty`, `JdbcProperty`, `PojoProperty`, or other concrete `Property` implementation.

For example:

```text
JsonStringProperty ─┐
MapStringProperty  ─┼─> TextValue
PojoStringProperty ─┘
```

The common vocabulary must remain representation-level. It must not contain business concepts such as:

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
PropertyValue
        ↓
Attribute interpretation
        ↓
semantic value
```

This establishes:

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

### Attribute

`Attribute<T>` describes a semantic coordinate of a model.

An attribute determines what semantic value is expected, how a represented property is interpreted, and which rules must hold for that value.

Conceptually:

```java
public interface Attribute<T> {

    AttributeName<T> name();

    ModelAttribute<T> bind(Property property);
}
```

An attribute obtains the primitive interpretation it needs from `PropertyValue`.

For example, a textual family may conceptually bind through:

```java
property.value().asText()
```

while a numeric family may use:

```java
property.value().asNumber()
```

This avoids `instanceof`, `Class<?>`, representation-specific property types, capability witness objects, and closed visitor methods inside semantic attributes.

Primitive semantic interpretations may include:

```text
TextAttribute
IntAttribute
DecimalAttribute
BooleanAttribute
```

while composition may add stronger semantics such as:

```text
NonNullAttribute
PositiveAttribute
EmailAttribute
MoneyAttribute
```

Interpretation and validation therefore remain object compositions rather than a sequence of external procedures.

### Attribute identity

Two attributes do not have the same identity merely because they expose the same Java type or textual name.

For example:

```text
Student.email
Teacher.email
```

remain distinct even if both produce `Email`.

Likewise, `Student.name` and `Course.name` remain distinct even if both produce textual values.

### AttributeName<T>

Attribute names may carry their semantic result type:

```java
AttributeName<Email> email;
AttributeName<Integer> age;
```

allowing typed APIs such as:

```java
Email email = model.value(emailName);
```

without casts in application code.

The generic type does not by itself provide runtime safety because of Java type erasure. The invariant must be established while constructing the `Model`: an `AttributeName<T>` may only refer to a compatible `ModelAttribute<T>` created through successful metadata binding.

### PropertyMapping

`PropertyMapping` belongs to the relationship between semantic metadata and a concrete data representation.

It does not belong intrinsically to `Data`: a `Data` object must remain valid without knowing that it will ever be associated with a model.

It also does not belong intrinsically to an `Attribute`: semantic attributes must remain unaware of physical representation coordinates.

Conceptually:

```java
public interface PropertyMapping {

    PropertyReference property(AttributeName<?> attribute);
}
```

The mapping therefore connects:

```text
AttributeName
     ↓
PropertyMapping
     ↓
PropertyReference
```

Different mapping strategies may express different representation conventions, for example:

```text
same name
UPPER_CASE
camelCase -> snake_case
explicit attribute -> property mapping
legacy field aliases
```

The same `Metadata` may consequently bind different data representations through different mappings, and the same `Data` may be interpreted through different semantic associations without becoming aware of them.

### Metadata

`Metadata` defines the semantic structure and invariants of a model.

Binding receives both represented data and the strategy that associates semantic attributes with representation coordinates:

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

This keeps addressing and interpretation as separate concerns:

```text
addressing:
AttributeName -> PropertyReference

interpretation:
Property -> PropertyValue -> semantic value
```

Property lookup therefore does not imply textual name equality. A semantic `Student.email` may map to JSON field `e_mail_address`, while `Student.birthDate` may map to position 7 in a positional representation.

Representation coordinates and semantic attribute identities remain separate concepts.

### Binding is construction

`Metadata.bind(Data, PropertyMapping)` establishes semantic validity while constructing the model.

A successful result must never require a subsequent:

```java
model.isValid()
```

or:

```java
validator.validate(model)
```

before use.

> **Metadata binding is construction, not validation after construction.**

Therefore:

> **A Model either exists in a valid state, or it does not exist.**

The representation of binding failures and whether multiple violations are accumulated remain separate API decisions.

### ModelAttribute

`ModelAttribute<T>` represents the successful binding of an `Attribute<T>` against concrete data.

Conceptually:

```java
public interface ModelAttribute<T> {

    AttributeName<T> name();

    T value();
}
```

Its value has already crossed the representation-to-semantics boundary and has already been validated by the attribute.

A `ModelAttribute` is therefore evidence that a concrete portion of represented data satisfies a semantic attribute.

### Model

A `Model` represents `Data` that has successfully acquired a semantic structure through `Metadata`.

Conceptually:

```java
public interface Model {

    Metadata metadata();

    Data data();

    <T> T value(AttributeName<T> name);
}
```

A `Model` differs fundamentally from a DTO. A DTO usually reproduces a data shape as Java state; a Model establishes a valid semantic interpretation over data.

The underlying information does not necessarily have to be copied into equivalent Java fields.

> **Model is trustworthy.**

### Access to underlying Data

A `Model` may retain access to the `Data` from which it was constructed.

This enables additional transformations without forcing all information into `ModelAttribute`s.

For example:

```text
Data
 ├─ id            → ModelAttribute
 ├─ status        → ModelAttribute
 ├─ description   → retained as Data
 └─ sourceNotes   → retained as Data
```

The Model can reason about `id` and `status` without requiring `description` or `sourceNotes` to become explicit semantic Java state merely to preserve them.

### PojoData and PojoModel

`PojoData` and `PojoModel` represent different concepts.

`PojoData` treats a Java object as a source representation and does not inherently claim that the Java class defines a semantic model.

`PojoModel` may instead compose `PojoData` with metadata derived from the class, annotations, reflection, or another class-description mechanism.

This preserves the principle that a Java class is one possible representation rather than the universal definition of data.

### Cross-model compatibility

Two distinct attributes may share a semantic type:

```text
Student.email : Attribute<Email>
Teacher.email : Attribute<Email>
Contact.email : Attribute<Email>
```

They remain separate attributes but may participate in type-safe mappings or projections.

Semantic value objects such as `Email`, `Money`, `StudentId`, or `CountryCode` can make such compatibility stronger than broad Java types such as `String`.

The exact projection API is outside the scope of this ADR.

### Rendering is orthogonal

Neither `Data` nor `Model` is required to implement a printing or serialization capability.

Output representation is independent from input representation. A `JdbcData` may legitimately be rendered as JSON.

Rendering should therefore be introduced through composition such as:

```text
JsonPrintableData(Data)
JsonPrintableModel(Model)
XmlPrintableData(Data)
```

rather than forcing `Data` or `Model` to implement output-format interfaces directly.

## Conceptual model

```text
                  REPRESENTATION
                       │
                       ▼
                     Data
                       │
                 Properties
                       │
                 PropertyValue
                       │
                       │ interpreted by
                       ▼
                   Attributes
                       │
                       │ defined by
                       ▼
                    Metadata
                       │
              + PropertyMapping
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

The coordinate relationship remains orthogonal:

```text
AttributeName
     ↓
PropertyMapping
     ↓
PropertyReference
     ↓
Data
```

## Fundamental principles

> **A Java class is one possible representation of data, not the definition of data itself.**

> **The presence of information does not imply the need for an equivalent Java attribute.**

> **Data should be represented without requiring its entire structure to become Java object state.**

> **Data is transformable.**

> **Data knows representation coordinates, not semantic coordinates.**

> **PropertyMapping belongs to the binding relationship.**

> **PropertyValue owns primitive representation interpretation. Attribute interpretation establishes meaning.**

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

> **Metadata binding is construction, not validation after construction.**

> **A Model either exists in a valid state, or it does not exist.**

> **Model is trustworthy.**

## Consequences

Forma avoids requiring a dedicated Java class for every shape of data entering or leaving an application.

Information irrelevant to current decisions can remain encapsulated in its source representation without becoming unnecessary Java fields.

Data may retain properties not currently represented as model attributes, avoiding accidental information loss while also avoiding artificial expansion of the semantic object model.

Different physical representations can participate in the same semantic model through different `PropertyMapping` strategies.

Partial data becomes a first-class concept rather than an invalid DTO.

Merge, projection, filtering, decoding, decryption, and similar operations can occur at the representation layer before semantic binding.

Primitive representation conversions are centralized in reusable `PropertyValue` implementations rather than duplicated across every representation-specific `Property`.

Semantic attributes no longer require `instanceof`, representation-specific property types, capability witness objects, or a closed interpreter whose future methods force unrelated attributes to implement impossible cases.

The common `PropertyValue` vocabulary becomes an explicit design boundary and must therefore remain deliberately small.

## Alternatives considered

### Represent every data shape with a POJO or DTO

Rejected as Forma's fundamental model.

This duplicates source structure in Java, often materializes properties which have no behavioral relevance, and encourages external procedural manipulation through accessors and utility classes.

POJOs remain supported as one representation through `PojoData`.

### Every Data has Metadata

Rejected.

Partial and intermediate information may not yet satisfy a complete semantic structure. `Data` exists independently; successful metadata binding produces a `Model`.

### Data directly exposes semantic attributes

For example:

```java
Property property(AttributeName<?> name);
```

or:

```java
<T> T value(Attribute<T> attribute);
```

Rejected as the fundamental `Data` API.

A `Data` object must not need to know that it will ever participate in a semantic model. Semantic-to-representation association is supplied separately through `PropertyMapping` during binding.

### Property uses a closed interpreter

For example:

```java
public interface Property {

    <T> T describe(PropertyValue<T> interpreter);
}
```

with an interpreter containing methods such as `text`, `number`, `boolean`, and so on.

Rejected.

Adding a new represented value kind would force every existing specialized interpreter, such as a textual attribute, to implement a method for a value kind it cannot meaningfully consume, usually only to reject it. That makes the interpreter vocabulary a closed sum and spreads unsupported-case methods across semantic classes.

### Representation-specific Property capability interfaces

For example:

```text
TextProperty
NumberProperty
```

combined with casts, `instanceof`, `Class<?>`, or capability witness objects.

Rejected for the core binding boundary.

These approaches either couple semantic attributes to runtime type inspection or introduce technical witness objects that do not represent a useful domain concept.

### Property exposes Object

Rejected because it shifts type interpretation and casts to clients.

### Property has one universal byte or textual representation

Rejected because it throws away information already available from source-specific representations and may introduce unnecessary serialization and parsing.

### Property conversion logic lives in every concrete Property

Rejected.

If `JsonStringProperty`, `JdbcVarcharProperty`, and `PojoStringProperty` all implement the same `asText`, `asNumber`, and related conversion logic independently, representation adapters duplicate behavior that belongs to the represented value itself.

Reusable value objects such as `TextValue` and `NumberValue` centralize that behavior.

### Model may exist in an invalid state

Rejected. Validity is a construction invariant.

### Data and Model implement output formats directly

Rejected. Rendering is an orthogonal interpretation that should be composed independently.

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

* which additional primitive interpretations, beyond text and number, belong in the fundamental `PropertyValue` vocabulary;
* exact conversion and failure semantics for incompatible `PropertyValue` interpretations;
* concrete `PropertyReference` kinds required by JSON, JDBC, POJO, and positional data;
* standard `PropertyMapping` strategies such as identity, explicit, case conversion, and naming conventions;
* the exact failure model of `Metadata.bind`;
* fail-fast versus accumulated validation violations;
* the definitive set of primitive `Attribute<T>` implementations and decorators;
* the exact runtime identity semantics of `AttributeName<T>`;
* whether binding remains lazy where possible or always evaluates every required attribute;
* whether `ModelAttribute` stores its semantic value or may itself remain an interpreted view;
* canonical model representation and equality;
* business identity based on combinations of attributes;
* typed projections between models;
* concrete `Data` transformation algebra;
* media-independent printing and serialization APIs.
