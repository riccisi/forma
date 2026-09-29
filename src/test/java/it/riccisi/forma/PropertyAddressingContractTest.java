package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.DataOf;
import it.riccisi.forma.mapping.ExplicitMapping;
import it.riccisi.forma.mapping.SameNameMapping;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.AttributeValueAt;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.field.NamedReference;
import it.riccisi.forma.field.TextValue;
import it.riccisi.forma.field.FieldOf;
import java.util.List;
import java.util.Map;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

final class PropertyAddressingContractTest {

    @Test
    void namedRepresentationDoesNotAssumeSemanticNameEquality() {
        assertThat(
            new EmailObservation(new NamedReference("e_mail_address")).value(),
            equalTo("alice@example.com")
        );
    }

    @Test
    void sameNameConventionDerivesRepresentationReference() {
        final AttributeName<String> email = new AttributeNameOf<>("email");
        assertThat(
            new AttributeValueAt<>(
                email,
                new ModelOf(
                    new MetadataOf(new StringAttribute(email)),
                    new DataOf(
                        new FieldOf(
                            new NamedReference("email"),
                            new TextValue(new TextOf("alice@example.com"))
                        )
                    ),
                    new SameNameMapping(NamedReference::new)
                )
            ).value(),
            equalTo("alice@example.com")
        );
    }

    @Test
    void positionalRepresentationUsesTheSameProtocol() {
        assertThat(
            new EmailObservation(new PositionalReference(7)).value(),
            equalTo("alice@example.com")
        );
    }

    @Test
    void nestedRepresentationUsesTheSameProtocol() {
        assertThat(
            new EmailObservation(new PathReference(List.of("contact", "email"))).value(),
            equalTo("alice@example.com")
        );
    }

    @Test
    void differentMappingsObserveTheSameData() {
        assertThat(
            new AlternativeEmailObservations().first(),
            equalTo("first@example.com")
        );
    }

    @Test
    void secondMappingObservesAlternativeValue() {
        assertThat(
            new AlternativeEmailObservations().second(),
            equalTo("second@example.com")
        );
    }

    @Test
    void bothModelsRetainTheSameData() {
        final AlternativeEmailObservations scenario = new AlternativeEmailObservations();
        assertThat(scenario.firstModel.data(), sameInstance(scenario.secondModel.data()));
    }

    private record PositionalReference(int value) implements FieldReference {
    }

    private record PathReference(List<String> segments) implements FieldReference {
    }

    private static final class EmailObservation {

        private final Model model;
        private final AttributeName<String> email;

        private EmailObservation(final FieldReference reference) {
            this.email = new AttributeNameOf<>("email");
            this.model = new ModelOf(
                new MetadataOf(new StringAttribute(this.email)),
                new DataOf(
                    new FieldOf(
                        reference,
                        new TextValue(new TextOf("alice@example.com"))
                    )
                ),
                new ExplicitMapping(Map.of(this.email, reference))
            );
        }

        String value() {
            return new AttributeValueAt<>(this.email, this.model).value();
        }
    }

    private static final class AlternativeEmailObservations {

        private final AttributeName<String> email;
        private final Model firstModel;
        private final Model secondModel;

        private AlternativeEmailObservations() {
            this.email = new AttributeNameOf<>("email");
            final FieldReference first = new NamedReference("email");
            final FieldReference second = new NamedReference("e_mail");
            final Metadata metadata = new MetadataOf(new StringAttribute(this.email));
            final Data data = new DataOf(
                new FieldOf(first, new TextValue(new TextOf("first@example.com"))),
                new FieldOf(second, new TextValue(new TextOf("second@example.com")))
            );
            this.firstModel = new ModelOf(
                metadata, data, new ExplicitMapping(Map.of(this.email, first))
            );
            this.secondModel = new ModelOf(
                metadata, data, new ExplicitMapping(Map.of(this.email, second))
            );
        }

        String first() {
            return new AttributeValueAt<>(this.email, this.firstModel).value();
        }

        String second() {
            return new AttributeValueAt<>(this.email, this.secondModel).value();
        }
    }
}
