# ADR-003 — Separate Object Construction from Data Observation

## Status

Proposed

## Context

Forma models objects that may represent information without owning or materializing that information.

A `Data` object may be backed by an immutable in-memory representation, but it may also observe a source whose contents can change independently. Likewise, objects such as `PropertyAt` and `AttributeOf` represent observations that can be described before the represented information is actually requested.

The current API applies two different construction rules.

`PropertyAt` composes a representation coordinate with `Data`:

```java
new PropertyAt(reference, data)
```

Construction does not require the referenced property to exist. The property is located when the `PropertyAt` is observed.

`AttributeOf` behaves similarly:

```java
new AttributeOf<>(name, model)
```

Construction describes which semantic attribute is to be observed. It does not establish its existence immediately.

`ModelOf`, however, currently behaves differently. Construction traverses all `Metadata`, maps every `AttributeName` to a `PropertyReference`, locates every property, interprets every required `PropertyValue`, validates every attribute, and materializes all `ModelAttribute` instances before the constructor returns.

This difference originated from the principle that an object should not be constructible in an invalid state. That principle remains important, but it conflates two distinct forms of validity:

1. the integrity of state owned by an object;
2. facts about information or reality observed through that object.

For example, an object representing a file may validly own a path even when no file currently exists at that path. Requiring existence during construction would prevent the same object from meaningfully answering whether the file exists. More importantly, a successful existence check during construction cannot guarantee future existence when the filesystem is mutable.

The same distinction applies to Forma. A structurally immutable `Data` may observe mutable information. Eagerly validating that information during construction observes one instant; unless construction also captures an immutable snapshot, the result cannot guarantee that subsequent observations see the same state.

Therefore construction-time observation may provide a stronger-looking guarantee than the object can actually maintain.

## Decision

Forma distinguishes **object integrity** from **observed validity**.

> **Construction establishes object integrity; observation establishes facts about observed data.**

### Construction validates owned state

A constructor is responsible for establishing the invariants of the state that constitutes the object itself.

Examples include:

```text
AttributeNameOf -> valid semantic name
Position        -> valid numeric position
ModelOf         -> Metadata + Data + PropertyMapping
PropertyAt      -> PropertyReference + Data
AttributeOf     -> AttributeName + Model
```

If the supplied state is intrinsically incapable of constituting the object, construction fails.

This does not imply that constructors must be mechanically free of every conditional check. The relevant distinction is semantic: construction establishes the integrity of the object being composed rather than performing the work represented by that object.

### Observation establishes conditions of represented state

Conditions whose truth depends on represented or external information are established when that information is observed.

Examples include:

```text
whether a PropertyReference exists in Data
whether a PropertyValue admits a requested interpretation
whether a represented value satisfies an Attribute constraint
whether an AttributeName can be observed in a Model
```

Such conditions may change independently when the underlying source is mutable. They are therefore not durable invariants of the immutable observer.

> **A constructor guarantees the integrity of the object, not the state of the world the object represents.**

### Model is a semantic view

A `Model` represents `Data` observed through `Metadata` using a `PropertyMapping`.

Conceptually:

```text
Metadata ───────┐
Data ───────────┼── Model
PropertyMapping ┘
                    |
                    | semantic observation
                    v
              ModelAttribute
                    |
                    +-- locate represented property
                    +-- interpret represented value
                    +-- enforce attribute semantics
```

Constructing a `Model` establishes this semantic relationship. It does not require every semantic observation to be executed immediately.

Therefore:

```java
new ModelOf(metadata, data, mapping)
```

must not, merely as a consequence of construction, interpret all values required by `metadata`.

A model is structurally valid when it has the collaborators required to perform its semantic observations.

> **A Model is a semantic view of Data through Metadata.**

### Semantic failure belongs to observation

A semantic observation may fail because the represented state cannot satisfy the requested semantics.

Existing reasons include:

```text
MissingProperty
UninterpretableValue
RejectedValue
```

These failures do not imply that the observer itself was structurally invalid.

The boundary that knows both semantic identity and representation coordinate remains responsible for enriching a low-level `BindingReason` with that context. `BindingFailure` therefore remains useful, but its meaning changes from a failure to construct the whole `Model` to a failure to establish a requested semantic observation.

The existing principle remains valid:

> **The object that understands a failure gives it meaning. The semantic observation boundary gives it context.**

### Interpretation is demand-driven

Construction of a model must not interpret represented values.

Observing one model attribute should interpret only the information necessary for that observation.

Consequently, represented information may remain uninterpreted even when it is described by `Metadata`, if no consumer requests the corresponding semantic observation.

This strengthens the existing principle:

> **Interpret what the application needs; preserve the rest as data.**

The interpretation boundary is no longer model construction. It is semantic observation.

### Laziness does not introduce mutable validity state

This decision does not introduce an internal lifecycle such as:

```text
unvalidated -> validated
```

A `Model`, `PropertyAt`, or `AttributeOf` remains an immutable composition of collaborators.

Observation may execute work and may fail. That does not require the observer to mutate.

Caching or snapshot semantics are separate concerns. If an application needs stable observations over a mutable source, that stability must be represented explicitly by an object providing snapshot or memoization semantics rather than being an accidental consequence of construction.

### Mutable sources make snapshot semantics explicit

A structurally immutable object may observe mutable information.

For example:

```text
t0  construct Model over DatabaseData
t1  database changes
t2  observe an attribute
```

The observation at `t2` concerns the state exposed by the `Data` according to that implementation's semantics.

If consumers require the state observed at `t0`, they need a `Data` representation with explicit snapshot semantics.

Eager validation without snapshot capture does not solve this problem; it merely validates a state that may no longer be the state subsequently observed.

## Consequences

`ModelOf` should retain `Metadata`, `Data`, and `PropertyMapping` rather than eagerly materializing all semantic attributes during construction.

Iteration over a `Model` may derive semantic attribute observations from `Metadata`. Producing those observation objects must not by itself force their values.

`AttributeOf` and `PropertyAt` are consistent with the same principle: they represent a requested observation and resolve represented state when their behavior requires it.

`ModelAttribute` implementations used by a model may themselves be observational objects rather than necessarily precomputed value holders.

`BindingFailure` documentation and creation points must describe semantic observation rather than whole-model construction.

Tests must distinguish construction from observation. In particular they should prove that:

- constructing a model does not interpret property values;
- iterating semantic observation objects does not necessarily interpret their values;
- requesting one semantic value interprets only the information required for that value;
- missing, uninterpretable, and rejected values fail at observation while preserving semantic and representation context;
- owned-state invariants continue to fail at construction.

The previous statement:

> **A Model either exists in a valid state, or it does not exist.**

is superseded because it does not distinguish object integrity from the validity of observed information.

The replacement is:

> **A Model always satisfies its structural invariants; semantic validity is established by its observations.**

The previous statement:

> **Metadata determines what must be understood for a Model to exist.**

is also superseded.

Metadata determines the semantic observations a Model can describe. It does not require all of them to be executed merely for the Model object to exist.

## Alternatives considered

### Eagerly validate every Metadata attribute during Model construction

Rejected.

This makes construction perform semantic work, differs from the observational semantics already used by `PropertyAt` and `AttributeOf`, and cannot provide a durable validity guarantee when `Data` observes mutable sources unless construction also captures a snapshot.

It also interprets values that a consumer may never observe.

### Require every observational object to prove existence during construction

Rejected.

Objects such as `PropertyAt` would have to locate their target during construction, and analogous objects representing external resources would have to establish transient facts merely to exist.

This confuses the validity of an observer with the current state of what it observes and makes predicates such as existence difficult or impossible to model naturally.

### Make Model mutable and validate on first use

Rejected.

Demand-driven observation does not require mutable validity state. The model remains an immutable composition. A semantic observation either produces its result or fails according to the represented state it observes.

### Eagerly materialize an immutable snapshot

Not rejected as a separate capability, but rejected as the universal meaning of `Model`.

Snapshot semantics can be valuable when temporal stability is required. They must be explicit because copying or materializing every representation would contradict Forma's ability to represent external, partial, large, streaming, or otherwise non-materialized information.

## Relationship to ADR-001

ADR-001 establishes that `Data` may be structurally immutable while representing mutable information and that represented information need not be eagerly materialized.

This decision extends those principles to semantic observation.

ADR-001 currently contains statements that equate Model construction with eager successful binding, including the claim that a Model either exists valid or does not exist. Those statements must be revised when this ADR is accepted.

The fundamental separation remains unchanged:

> **Data knows representation. Metadata knows semantics. PropertyMapping relates their coordinates.**

This ADR changes when that relationship is interpreted, not the responsibilities of those abstractions.
