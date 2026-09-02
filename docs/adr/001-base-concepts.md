# ADR-001 — Treat Data as a First-Class Object-Oriented Domain

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

The object becomes primarily a passive state container while other objects extract, inspect, transform, validate, copy, and render its contents.

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

The problem is not that Java classes may contain fields. A Java class can be a perfectly valid representation of information.

The problem is making an equivalent Java object structure the mandatory ontology of the data.

> **A Java class is one possible representation of data, not the definition of data itself.**

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

A `Data` object encapsulates represented information, but individual portions of that information only need to become semantic Java values when an interpretation requires them.

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

Forma is inspired by parsing objects: rather than parsing a representation into a passive object and subsequently working on exposed state, the object encapsulating the representation remains responsible for representing that information.

This does not require mandatory laziness. It establishes the stronger property that represented information does not have to be converted into an equivalent Java object graph merely to participate in the application.

## Data as an application domain

The problems above are not merely consequences of choosing an inconvenient Java container type.

They reveal that data itself has a domain.

Applications that manipulate significant amounts of information repeatedly solve problems such as:

```text
locating values
interpreting values
validating values
combining partial data
transforming representations
describing structure
persisting data
rendering data
editing data
relating data
```

When data is represented primarily as arbitrary POJOs, these concerns rarely share an explicit object model.

Instead, each technical layer tends to recreate its own understanding of the same information:

```text
controller
validator
mapper
serializer
ORM entity
repository
form model
schema
CRUD service
```

These components often collaborate indirectly through Java classes whose fields happen to reproduce a particular data shape.

Forma takes a different view:

> **Forma applies domain modeling to data itself.**

`Data`, `Property`, `Attribute`, `Metadata`, and `Model` are therefore not abstractions introduced merely to avoid DTO boilerplate.

They form a shared object-oriented vocabulary through which components concerned with data can collaborate.

The purpose is not only to represent data more faithfully, but to make knowledge already discovered about that data explicit, composable, and reusable.

The resulting abstractions are valuable not because they are more philosophically object-oriented, but because they allow common data-oriented application concerns to be modeled once instead of being statically re-coded around every new POJO shape.

## Metadata as first-class knowledge

`Data` is first-class represented information.

`Metadata` is first-class knowledge about how that information can be understood.

Neither has to originate from a Java class.

A metadata object may be declared directly in application code:

```text
StudentMetadata
CourseMetadata
InvoiceMetadata
```

but it may equally be obtained from another source:

```text
DatabaseMetadata
RemoteMetadata
ConfiguredMetadata
GeneratedMetadata
```

For example, a visual metadata designer could persist a description to a database, from which a `DatabaseMetadata` object later represents the same semantic knowledge to the application.

That metadata may then be interpreted by independent components concerned with different aspects of the same data:

```text
                         ┌── model construction
                         ├── validation
                         ├── transformations
                         ├── output representations
Data + Metadata ─────────┼── persistence
                         ├── generic CRUD
                         ├── dynamic editing
                         ├── UI generation
                         ├── schema/documentation
                         └── domain-specific capabilities
```

Forma does not require the core `Metadata` contract to know about all these interpretations.

A metadata object should be rich enough to be interpreted, but must not become a centralized procedural descriptor containing every capability every consumer may ever require.

> **Metadata should be rich enough to be interpreted, but not centralized enough to know every interpretation.**

Capabilities may instead emerge from focused `Attribute` implementations, decorators, metadata implementations, and higher-level components that understand a particular vocabulary.

For example, persistence-oriented extensions may eventually describe identifiers or relationships, while UI-oriented extensions may describe editing or presentation concerns. Such vocabularies should compose with the same fundamental model without forcing persistence or UI concepts into Forma core.

## Decision drivers

Forma should:

* make data itself a first-class object-oriented application concept;
* make semantic descriptions of data first-class and reusable;
* allow multiple independent components to collaborate through the same data and metadata abstractions;
* allow both `Data` and `Metadata` to originate from code, external systems, databases, configuration, or other representations;
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
* allow capabilities to emerge through focused object composition rather than centralized procedural descriptors;
* make common data-oriented application concerns reusable once their semantics have been modeled;
* support both interpretation of incoming representations and generation of outgoing representations;
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

They separate concerns that must not collapse into one another:

```text
representation coordinates
representation values
semantic meaning
semantic descriptions
successful semantic interpretation
```

### Data

`Data` represents information without claiming conformance to a particular business structure.

The fundamental contract is intentionally small:

```java
public interface Data extends Iterable<Property> {
}
```

A `Data` may be complete, partial, projected, merged, filtered, dynamically backed by another source, or unrelated to any known business model.

Each iterated `Property` carries its own representation coordinate. Lookup is therefore derivable from observation and does not need to be prescribed by the fundamental `Data` contract.

A generic locating object can express that operation independently:

```java
new PropertyAt(reference, data)
```

`Data` deliberately does not accept `AttributeName` as a lookup coordinate. A data representation must be able to exist independently from any `Model` or `Metadata` that may later be associated with it.

It therefore speaks only in representation coordinates.

> **Data is transformable.**

### Data does not require eager materialization

A `Data` implementation is not required to extract every property from its underlying source when constructed.

For example:

```java
new JsonData(json)
```

may retain the JSON representation itself. If no consumer ever needs to interpret `description`, there is no inherent requirement for Forma to convert that value into a Java `String`.

This is not mandatory laziness. It is the stronger architectural property that:

> **Forma does not require representation to be materialized into an equivalent Java object graph.**

Concrete implementations remain free to cache, materialize, stream, or lazily interpret information according to their own semantics.

### Data may be structurally immutable while representing mutable information

A `Data` object's identity and configuration may be immutable while the source it represents changes independently.

Forma does not equate structural object immutability with temporal stability of the represented source. Implementations requiring snapshot semantics must establish those semantics explicitly.

### Data composition

Because `Data` does not imply semantic validity, it may be transformed before model construction.

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

`MergedData` defines merge semantics only. It does not decide whether the resulting information constitutes a valid Student. That responsibility belongs to the semantic association established through `Metadata` and model construction.

Keeping `Data` iterable over `Property` is important because properties remain first-class representation objects over which compositions can operate.

### PropertyReference

`PropertyReference` is a coordinate understood by a concrete `Data` representation.

```java
public interface PropertyReference {
}
```

It is intentionally opaque and separate from `AttributeName`.

Possible representations may use:

```text
name
position
path
JDBC column
JSON pointer
POJO member
```

Forma core knows that data has coordinates, not what shape those coordinates have.

### Property

A `Property` is an addressable, individually interpretable portion of represented data.

```java
public interface Property {

    PropertyReference reference();

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

The initial contract is deliberately small:

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
TextValue("42").asText()    -> "42"
TextValue("42").asNumber()  -> 42
NumberValue(42).asNumber()   -> 42
NumberValue(42).asText()     -> "42"
```

A conversion is valid when the represented information admits that interpretation. A textual value containing a valid number may therefore legitimately provide `asNumber()`. A textual value that cannot be interpreted numerically fails at that representation boundary.

This centralizes primitive conversion logic in reusable value objects instead of repeating it in every `JsonProperty`, `JdbcProperty`, `PojoProperty`, or other concrete `Property` implementation.

The common vocabulary must remain representation-level. It must not contain business concepts such as:

```text
Email
Money
StudentId
```

Those belong to semantic interpretation.

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

`Attribute<T>` describes a semantic coordinate and the rules required to establish a semantic value from a represented property.

```java
public interface Attribute<T> {

    AttributeName<T> name();

    ModelAttribute<T> from(Property property);
}
```

An attribute obtains the primitive interpretation it needs from `PropertyValue`.

For example, a textual family may interpret through:

```java
property.value().asText()
```

while a numeric family may use:

```java
property.value().asNumber()
```

Primitive semantic interpretations currently include objects such as:

```text
StringAttribute
IntegerAttribute
```

while composition may add stronger semantic constraints, for example:

```text
NonBlankAttribute
```

The important boundary is that primitive attributes interpret representation while attribute decorators constrain already established semantics.

This avoids `instanceof`, `Class<?>`, representation-specific property types, capability witness objects, and closed visitor methods inside semantic attributes.

It also creates a natural extension point for additional data-oriented vocabularies. An object concerned with persistence, relations, editing, rendering, or another specialized concern may interpret focused attribute objects or decorators without adding unrelated methods to every `Attribute`.

### AttributeName<T>

`AttributeName<T>` is the typed semantic name of an attribute within a `Metadata`.

```java
public interface AttributeName<T> extends Text {
}
```

An `AttributeName` is a value object, not a globally unique application identity.

Therefore a Student metadata and a Teacher metadata may both contain an attribute named `email` without conflict.

```text
Student metadata -> "email"
Teacher metadata -> "email"
```

Equal textual names represent the same attribute name within the metadata context in which they are used.

> **An AttributeName identifies an attribute within Metadata, not globally across the application.**

The generic type gives compile-time information to typed consumers, while the actual type invariant is established when the model is constructed.

### PropertyMapping

`PropertyMapping` belongs to the relationship between semantic metadata and a concrete data representation.

It does not belong intrinsically to `Data`: a `Data` object must remain valid without knowing that it will ever be associated with a model.

It also does not belong intrinsically to an `Attribute`: semantic attributes must remain unaware of physical representation coordinates.

```java
public interface PropertyMapping {

    PropertyReference property(AttributeName<?> attribute);
}
```

The mapping connects:

```text
AttributeName
     ↓
PropertyMapping
     ↓
PropertyReference
```

Different mapping strategies may express different representation conventions, for example:

```text
same textual name
camelCase -> snake_case
explicit attribute -> property mapping
legacy field aliases
```

The same `Metadata` may consequently participate in different data representations through different mappings, and the same `Data` may be interpreted through different semantic associations without becoming aware of them.

A same-name convention still requires a representation-specific way to construct a `PropertyReference`:

```java
new SameNameMapping(JsonField::new)
```

because textual equality is a mapping convention, not a universal representation coordinate.

> **Same name is a mapping convention, not a universal representation coordinate.**

### Metadata

`Metadata` describes semantic structure by exposing the attributes that define it.

```java
public interface Metadata extends Iterable<Attribute<?>> {
}
```

`Metadata` does not perform binding itself.

Binding is how a `Model` comes into existence, so model construction owns the association between metadata, represented data, and the mapping that relates their coordinates.

A concrete in-memory metadata can be composed directly:

```java
new MetadataOf(
    new NonBlankAttribute(name),
    age
)
```

but that is only one possible metadata representation.

A metadata object may instead be backed by configuration, a database, a remote source, generated structures, or application-specific objects. The important contract is the semantic description it exposes, not where that description originated.

> **Metadata describes semantics; it does not prescribe where semantic knowledge must live.**

### Model construction

`ModelOf` establishes the association between a `Metadata`, a `Data`, and a `PropertyMapping`.

Conceptually:

```java
new ModelOf(metadata, data, mapping)
```

Construction performs the semantic path:

```text
Metadata
   ↓
AttributeName
   ↓
PropertyMapping
   ↓
PropertyReference
   ↓
PropertyAt(reference, Data)
   ↓
Attribute.from(Property)
   ↓
ModelAttribute
   ↓
Model
```

This keeps addressing and interpretation as separate concerns:

```text
addressing:
AttributeName -> PropertyReference -> Property

interpretation:
Property -> PropertyValue -> semantic value
```

Property lookup therefore does not imply textual name equality. A semantic `email` may map to JSON field `e_mail_address`, while a semantic `birthDate` may map to position 7 in a positional representation.

Representation coordinates and semantic attribute names remain separate concepts.

### Binding is construction

Model construction establishes semantic validity while creating the model.

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

More precisely in the current object model:

> **Binding is not something Metadata does. Binding is how a Model comes into existence.**

Therefore:

> **A Model either exists in a valid state, or it does not exist.**

The representation of binding failures and whether multiple violations are accumulated remain separate API decisions.

### ModelAttribute

`ModelAttribute<T>` represents a successful semantic binding.

```java
public interface ModelAttribute<T> {

    AttributeName<T> name();

    T value();
}
```

Its value has already crossed the representation-to-semantics boundary and has already satisfied the corresponding attribute semantics.

A `ModelAttribute` is therefore evidence that a concrete portion of represented data satisfies a semantic attribute.

### Model

A `Model` represents `Data` that has successfully acquired a semantic interpretation through `Metadata`.

```java
public interface Model extends Iterable<ModelAttribute<?>> {

    Metadata metadata();

    Data data();
}
```

A `Model` differs fundamentally from a DTO. A DTO usually reproduces a data shape as Java state; a Model establishes a valid semantic interpretation over data.

The underlying information does not have to be copied into equivalent Java fields.

Typed lookup is a derived observation rather than a fundamental method on `Model`:

```java
String name = new AttributeOf<>(nameAttribute, model).value();
```

This keeps `Model` small while preserving the type invariant established during construction.

> **Model is trustworthy.**

### Access to underlying Data

A `Model` retains access to the `Data` from which it was constructed.

This enables further data-oriented operations without forcing all information into `ModelAttribute`s.

For example:

```text
Data
 ├─ id            → ModelAttribute
 ├─ status        → ModelAttribute
 ├─ description   → retained as Data
 └─ sourceNotes   → retained as Data
```

The Model can expose semantic `id` and `status` without requiring `description` or `sourceNotes` to become explicit semantic Java state merely to preserve them.

> **Interpret what the application needs; preserve the rest as data.**

### Metadata as a shared language for capabilities

Once information about data has been modeled explicitly, multiple components can rely on the same description rather than independently rediscovering the shape of an arbitrary Java object.

A persistence-oriented component might interpret metadata to discover identifiers or relationships.

A dynamic editing component might interpret metadata to determine what may be edited and how values must be understood.

A CRUD component might combine metadata with generic `Data` sources and output representations.

A schema or documentation component might expose the same semantic description externally.

A renderer may combine `Model`, `Metadata`, and a target media representation.

These components are not part of the fundamental contracts merely because they are possible. The architectural point is that Forma gives them a coherent shared vocabulary on which they can be modeled.

This is different from a collection of unrelated utilities operating on arbitrary POJOs.

> **Common data-oriented capabilities should collaborate through modeled data semantics, not through accidental Java structure.**

### Specialized vocabularies

Forma core must not anticipate every characteristic that may eventually be associated with data.

For example, future specialized vocabularies may model concepts such as:

```text
primary identifiers
relations
persistence characteristics
editing characteristics
presentation characteristics
migration characteristics
```

These should emerge through focused objects and compositions rather than by continuously expanding `Attribute` or `Metadata` into configuration bags.

A hypothetical design such as:

```java
boolean primaryKey();
boolean required();
boolean editable();
String label();
String sqlType();
```

on every attribute would merely recreate procedural metadata in object syntax.

Forma instead favors objects whose types and composition carry the relevant semantics.

> **The type carries the invariant.**

### Input and output representations

Forma initially emphasizes interpretation of represented input, but output generation is an equally important consumer of the same model.

The desired architecture is not limited to:

```text
external representation
        ↓
       Data
        ↓
      Model
```

The wider ecosystem may support both directions:

```text
representation
      ↕
    Data
      ↕
   Metadata
      ↕
    Model
      ↕
representation
```

The two directions do not require mechanically symmetrical APIs. Their commonality is that they operate on the same explicit model of data rather than requiring an intermediate application-specific POJO shape.

### Rendering is orthogonal but primary

Neither `Data` nor `Model` is required to implement a printing or serialization capability.

Output representation is independent from input representation. A `JdbcData` may legitimately be rendered as JSON.

Rendering should therefore be introduced through composition such as:

```text
JsonPrintableData(Data)
JsonPrintableModel(Model)
XmlPrintableModel(Model)
```

rather than forcing `Data` or `Model` to implement output-format interfaces directly.

Rendering is therefore orthogonal to the core model, but it is a primary consumer of that model.

> **Output media is not part of Data identity; it is an interpretation of represented or semantic information.**

### PojoData and application objects

A Java object remains a valid source representation.

`PojoData` may treat an arbitrary Java object as a source of properties through reflection or another member-discovery mechanism without claiming that the Java class defines the semantic model.

Likewise, applications remain free to introduce classes such as `Student`, `Course`, or `Invoice` when those classes represent useful domain behavior.

Forma does not reject domain objects.

It rejects the assumption that every piece of information must first be duplicated into a passive Java structure before domain behavior, persistence, validation, rendering, or other data-oriented capabilities can operate on it.

A domain object may therefore consume or wrap a `Model` instead of reproducing the complete source data shape as fields.

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
                            │ described by
                            ▼
                         Metadata
                            │
                    + PropertyMapping
                            │
                            │ construction
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

The wider ecosystem surrounds the same model rather than inventing unrelated representations for each technical concern:

```text
                         ┌── Data adapters
                         ├── transformations
                         ├── model construction
                         ├── persistence
Data / Metadata / Model ─┼── CRUD
                         ├── rendering
                         ├── dynamic UI
                         ├── schemas
                         └── domain-specific interpreters
```

## Fundamental principles

> **Forma applies domain modeling to data itself.**

> **A Java class is one possible representation of data, not the definition of data itself.**

> **The presence of information does not imply the need for an equivalent Java attribute.**

> **Data should be represented without requiring its entire structure to become Java object state.**

> **Data is first-class represented information. Metadata is first-class knowledge about how that information can be understood.**

> **Neither Data nor Metadata has to originate from a Java class.**

> **Data is transformable. Model is trustworthy.**

> **Data knows representation. Metadata knows semantics. PropertyMapping relates their coordinates.**

> **Forma core knows that data has coordinates, not what shape those coordinates have.**

> **PropertyValue owns primitive representation interpretation. Attribute interpretation establishes meaning.**

> **Property transformations operate on representation. Attribute interpretation establishes meaning.**

> **An AttributeName identifies an attribute within Metadata, not globally across the application.**

> **Same name is a mapping convention, not a universal representation coordinate.**

> **Binding is not something Metadata does. Binding is how a Model comes into existence.**

> **A Model either exists in a valid state, or it does not exist.**

> **Interpret what the application needs; preserve the rest as data.**

> **Metadata should be rich enough to be interpreted, but not centralized enough to know every interpretation.**

> **Common data-oriented capabilities should collaborate through modeled data semantics, not through accidental Java structure.**

## Consequences

Forma avoids requiring a dedicated Java class for every shape of data entering or leaving an application.

Information irrelevant to current decisions can remain encapsulated in its source representation without becoming unnecessary Java fields.

Data may retain properties not currently represented as model attributes, avoiding accidental information loss while also avoiding artificial expansion of the semantic object model.

Different physical representations can participate in the same semantic model through different `PropertyMapping` strategies.

Partial data becomes a first-class concept rather than an invalid DTO.

Merge, projection, filtering, decoding, decryption, and similar operations can occur at the representation layer before semantic model construction.

Primitive representation conversions are centralized in reusable `PropertyValue` implementations rather than duplicated across every representation-specific `Property`.

Semantic attributes no longer require `instanceof`, representation-specific property types, capability witness objects, or a closed interpreter whose future methods force unrelated attributes to implement impossible cases.

The common `PropertyValue` vocabulary becomes an explicit design boundary and must therefore remain deliberately small.

Metadata becomes reusable semantic knowledge rather than an implementation detail tied to one Java class.

Multiple components can collaborate around the same data description without requiring each component to maintain its own DTO, entity, form model, schema, or reflection convention.

Metadata may be declared statically, generated, loaded from a database, obtained remotely, or represented by other objects without changing the fundamental contracts consumed by data-oriented components.

Persistence, CRUD, dynamic UI, rendering, documentation, migration, and other capabilities can be modeled as focused interpreters or compositions around the shared Forma vocabulary.

This does not imply that Forma core must implement every such capability. It establishes the domain model that allows those capabilities to remain coherent when they are implemented.

The normalized core API may be more explicit than a convenience API built around one concrete Java class. That explicitness is intentional: it preserves the distinctions necessary for independent representations and higher-level components to compose correctly.

Ergonomic application-specific envelopes and adapters may be built above the core without collapsing those distinctions.

## Alternatives considered

### Represent every data shape with a POJO or DTO

Rejected as Forma's fundamental model.

This duplicates source structure in Java, often materializes properties which have no behavioral relevance, and encourages external procedural manipulation through accessors and utility classes.

POJOs remain supported as one possible representation through objects such as `PojoData`.

### Treat DTOs, validators, mappers, serializers, and repositories as unrelated application concerns

Rejected as the conceptual basis for data-oriented systems.

Although those components may remain independently implemented, many of them repeatedly need knowledge about the same data. Without a shared domain model, that knowledge is duplicated in annotations, reflection rules, mapping definitions, validation code, schema definitions, form definitions, and persistence configuration.

Forma models the underlying data concepts so focused components can collaborate through shared semantics instead.

### Every Data has Metadata

Rejected.

Partial and intermediate information may not yet satisfy a complete semantic structure. `Data` exists independently; successful construction establishes a `Model` associated with `Metadata`.

### Metadata owns construction

Rejected.

`Metadata` describes semantics. Model construction establishes that concrete `Data` satisfies those semantics through a particular `PropertyMapping`.

Putting construction behavior on `Metadata` would conflate description with the act that creates a valid semantic association.

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

A `Data` object must not need to know that it will ever participate in a semantic model. Semantic-to-representation association is supplied separately through `PropertyMapping`.

### Data prescribes lookup as a fundamental method

Rejected.

`Data` is iterable over addressable `Property` objects, so lookup can be represented independently by an object such as `PropertyAt`. This keeps the fundamental representation contract smaller and allows specialized data implementations to optimize internally without changing core semantics.

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

### Attribute or Metadata becomes a universal configuration bag

Rejected.

Adding flags and getters for every persistence, UI, rendering, validation, relation, migration, and application concern would centralize all possible interpretations into one procedural descriptor.

Specialized semantics should instead be represented by focused objects, decorators, metadata implementations, or extension vocabularies understood only by the components that need them.

### AttributeName is globally unique

Rejected.

An `AttributeName` identifies an attribute within a metadata context. Context already supplied by `Metadata` must not be redundantly encoded into every attribute name merely to obtain global identity.

### Model may exist in an invalid state

Rejected. Validity is a construction invariant.

### Data and Model implement output formats directly

Rejected.

Rendering is an orthogonal interpretation that should be composed independently, while remaining a primary consumer of the shared data and metadata model.

## Scope

Forma is an independent library.

Its fundamental semantic model must not depend on:

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
UI frameworks
```

Representation-specific and capability-specific integrations may depend on external technologies in separate artifacts when concrete implementations justify that separation.

The library is named:

# **Forma**

with the intended root package:

```text
it.riccisi.forma
```

and the positioning:

> **Forma — an object-oriented model for data.**

A more complete architectural statement is:

> **Forma makes data and its semantic description first-class objects so data-oriented capabilities can collaborate through a shared object model.**

## Open questions

The following details remain deliberately unresolved:

* which additional primitive interpretations, beyond text and number, belong in the fundamental `PropertyValue` vocabulary;
* exact conversion and failure semantics for incompatible `PropertyValue` interpretations;
* concrete `PropertyReference` kinds required by JSON, JDBC, POJO, positional, and other data representations;
* additional standard `PropertyMapping` strategies beyond the current same-name convention;
* exact binding failure semantics;
* missing, optional, and required property semantics;
* fail-fast versus accumulated binding violations;
* the definitive set of primitive `Attribute<T>` implementations and decorators;
* metadata invariants such as duplicate attribute names;
* canonical model representation and equality;
* business identity based on combinations of attributes;
* typed projections between models;
* concrete `Data` transformation algebra;
* media-independent rendering and serialization APIs;
* which specialized vocabularies should exist outside Forma core;
* how application-declared, database-backed, generated, and remote Metadata implementations should be authored and located;
* how persistence-oriented concepts such as identifiers and relations should compose with semantic attributes without becoming core configuration flags;
* how dynamic UI and editing components should consume semantic and specialized metadata without coupling core contracts to presentation concerns.
