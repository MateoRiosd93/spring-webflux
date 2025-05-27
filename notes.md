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

**@AllArgsConstructor** Permite crear constructor con todos los atributos

**@NoArgsConstructor** Permite crear constructor vacio

### Repository
Para crear las interfaces del repository de estos modelos implementaremos en este caso con mongo 
que permite la reactividad la interface ReactiveMongoRepository<Model, Tipo del id>
esta recibe el modelo y el tipo del id del modelo que para este caso mongo maneja los id tipo String
esta interface nos permitira implementar consultas como findAll, findById, save, delete, update...y 
tambien permite crear consultas personalizadas utilizando palabras clave

_Ejemplo_ Se requiere una consulta que obtenga un valor por el nombre 
en este caso podriamos implementar un metodo que retorna un Mono<Modelo> findByName
By: palabra reservada y Name: nombre del atributo por el cual queremos hacer la consulta.

Nota: En caso de querer una consulta especifica aca seria el lugar en donde la
declarariamos.

_Ejemplo_

1. Mono< Product > findByName(String name);

Documentacion
[**Métodos de consulta específicos de MongoDB**](https://docs.spring.io/spring-data/mongodb/reference/mongodb/repositories/query-methods.html)

### Service
A la hora de crear nuestro servicios la idea es crear una interface la cual sera el contrato 
que implementara nuestro @Service con los metodos que se implementaran en el.

En la interface definiremos todos los metodos que se implementaran 

_Ejemplo de metodos_

1. Flux< Product > findAll();
2. Mono< Product > save(Product product);
3. Mono< Void > delete(Product product);

A la hora de crear nuestra clase para el servicio 
usaremos siempre la anotacion de springboot @Service
Aca inyectaremos nuestro Repository, y como buena practica 
haremos uso de la anotacion que nos proporciona Lombok **@RequiredArgsConstructor**
la cual nos permite hacer inyeccion de dependencias de una manera facil 
tener presente que la declaracion debe ser **private final**.

### Controller/RestController
 