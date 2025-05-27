## Apuntes SpringBoot WebFlux

### Dependencias para realizar una api rest con webflux

1. Spring Data Reactive MongoDB
2. Spring Reactive Web

**Nota:** _En este caso puntual usamos una BD reactiva (Mongo permite estos flujos reactivos)
y Spring Reactive Web para crear apis reactivas_

### Dependencias para utiles para proyectos Spring boot

1. LombokLombok
2. Spring Boot DevTools

### Models/Documents
A la hora de crear los Models en este caso al usar mongo hacemos referencia a Documents
se crea la clase y se coloca la anotacion @Document@Document(collection = "products")
con el respectivo nombre de la collection

Importante a la hora de crear Getters y Setters y contructores vacio o con argumentos
utilizamos la dependencia de Lombok la cual es super util para estos casos.

**@Data** Inyecta varias anotaciones de Lombok (@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor),
entonces ahorra anotar cada uno de estos en la clase.

**@Build** Nos permite crear un instancia de una clase propiedad por propiedad. (Esto nos sirve para 
cuando queremos crear una instancia inicializandola con algunas de las propiedades). Evitando asi tener varios constructores.

