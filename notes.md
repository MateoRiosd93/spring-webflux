## Apuntes SpringBoot WebFlux

---
## 📚 Tabla de Contenidos

- [Apuntes SpringBoot WebFlux](#apuntes-springboot-webflux)
- [Dependencias webflux](#dependencias-webflux)
- [Dependencias utiles para proyectos Springboot](#dependencias-utiles-para-proyectos-springboot)
- [Models Documents](#models-documents)
- [Repository](#repository)
- [Service](#service)
- [Controller/RestController](#controllerrestcontroller)
---

### Dependencias webflux

1. Spring Data Reactive MongoDB
2. Spring Reactive Web

**Nota:** _En este caso puntual usamos una BD reactiva (Mongo permite estos flujos reactivos)
y Spring Reactive Web para crear apis reactivas_

---
### Dependencias utiles para proyectos Springboot

1. LombokLombok
2. Spring Boot DevTools

---
### Models Documents
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

---
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

---
### Service
A la hora de crear nuestro servicios la idea es crear una interface la cual sera el contrato 
que implementara nuestro @Service con los metodos que se implementaran en el.

En la interface definiremos todos los metodos que se implementaran 

_Ejemplo de metodos_

```
Flux<Product> findAll();
Mono<Product> save(Product product);
Mono<Void> delete(Product product);
```
A la hora de crear nuestra clase para el servicio 
usaremos siempre la anotacion de springboot @Service
Aca inyectaremos nuestro Repository, y como buena practica 
haremos uso de la anotacion que nos proporciona Lombok **@RequiredArgsConstructor**
la cual nos permite hacer inyeccion de dependencias de una manera facil 
tener presente que la declaracion debe ser **private final**.

---
### Controller/RestController
A la hora de crear nuestros controladores, en springboot tenemos dos anotaciones para esto 
@Controller y @RestController es importante resaltar que se recomienda utilizar @RestController
ya este tambien inyecta a la clase @ResponseBody y @Controller, es decir simplifica la forma de usar
unicamente @Controller ya que toca especificar el @ResponseBody en nuestros methods

**Importante** a la hora de utilizar nuestro service recordar 
hacer uso de la anotacion @RequiredArgsConstructor y declarar nuestro servicio 
private final asi inyectaremos nuestro service de la mejor manera.
_**Hacer siempre uso de la interface, en ves de su implementacion**_

**@RequestMapping:**
Aca debemos hacer uso tambien del @RequestMapping en donde colocaremos el path nuestro controlador
es imporntante o se recomienda colocar /api/ y ademas de esto el versionamiento 
ejemplo /api/v1

**Methods**

```
@GetMapping 
@PostMapping
@PutMapping
@DeleteMappig
```

Es importante, saber que metodo aplicar a nuestros controlladores 
como ejemplo de un mal uso seria usar un @GetMapping para una eliminacion
ya que de esta manera pues podriamos obtener el id y hacer referencia al service 
que me elimina el elemento, funcionaria si pero digamos que nos es lo adecuado
en este caso se deberia de usar el @DeleteMapping.

**@PathVariable:**
Nos sirve para obtener informacion de la ruta, es importante 
que el parametro que se mapea a recibir en el metodo se llame igual que la variable 
a recibir en la ruta, en caso de querer llamarlo diferente, usaremos la propieda 
name del @PathVariable.

Ejemplo

```
@GetMapping("/{id}")
public Mono<Product> getProduct(@PathVariable String id) {
    return productService.findById(id);
}
```

**@RequestParams:**
Nos ayuda a extraer los parametros que se pueden llegar a mandar por la ruta.

Ejemplo

```
http://localhost:8080/spring-mvc-basics/api/foos?id=abc
----
ID: abc
```
```
@PostMapping("/api/foos")
@ResponseBody
public String addFoo(@RequestParam(name = "id") String fooId, @RequestParam String name) { 
    return "ID: " + fooId + " Name: " + name;
}
```

**@RequestBody:**
asigna el cuerpo de HttpRequest a un objeto de transferencia o dominio, lo que permite 
la deserialización automática del cuerpo de HttpRequest entrante en un objeto Java.

Ejemplo

```
@PostMapping("/request")
public ResponseEntity postController(
  @RequestBody LoginForm loginForm) {
 
    exampleService.fakeAuthenticate(loginForm);
    return ResponseEntity.ok(HttpStatus.OK);
}
```

**Nota:** Tener presente como se deben usar los DTO y los models 
en este caso entre la comunicacion de nuestro controller y service 
se haria uso de los DTO

Flujo tipico recomendado:

```
[CLIENTE]
   ⇅
[CONTROLLER] ⇄ [DTOs]
   ⇅
[SERVICE] ⇄ [MODELS/ENTITIES]
   ⇅
[REPOSITORY]

```

_Para mapear de DTO a models y viceversa podemos usar librerias como
MapStruct o ModelMapper._