Tôi làm repo sau với mục đích học về Spring Boot là chính.

Spring Boot là một framework mạnh mẽ giúp xây dựng các ứng dụng Java một cách nhanh chóng và dễ dàng hơn. 
Nó cung cấp một loạt các tính năng và công cụ để phát triển ứng dụng web, dịch vụ RESTful, và nhiều loại ứng dụng khác.

---

Trong quá trình học tập, Taskhub là dự án "xương sống" tôi làm xuyên suốt để áp dụng các kiến thức về Spring Boot.
Trong README này, tôi sẽ trình bày nội dung theo từng tuần mà tôi học được.

TaskHub – API quản lý công việc cá nhân

Tính năng: đăng ký/đăng nhập (JWT), CRUD task, gán nhãn, lọc/paginate, tệp đính kèm (tuỳ chọn), audit log, metrics/health, Docker compose (app+db+redis).

---

# Bài 1: “Hello Spring Boot”
Bài này tôi làm quen với Spring Boot, tạo project, cấu hình Maven, chạy ứng dụng và tạo endpoint đơn giản.

Ở bài này, tôi tìm hiểu được một số điểm như dưới đây.

---
1) Các loại *Mapping trong Spring MVC

Gốc là @RequestMapping, các annotation còn lại là “rút gọn” theo HTTP method:

- @GetMapping("/tasks")
   
   Lấy dữ liệu. Safe và idempotent (gọi nhiều lần không đổi trạng thái).
Dùng cho list/detail, tìm kiếm, health-check.

- @PostMapping("/tasks")

   Tạo mới. Không idempotent (mỗi lần gọi có thể tạo 1 bản ghi). Trả 201 khi tạo thành công.

- @PutMapping("/tasks/{id}")

   Cập nhật toàn bộ tài nguyên. Idempotent (gọi lại kết quả như nhau).

- @PatchMapping("/tasks/{id}")

   Cập nhật một phần. Không đảm bảo idempotent (tuỳ bạn thiết kế).

- @DeleteMapping("/tasks/{id}")

   Xoá. Idempotent (xoá lần 2 vẫn coi như thành công/204).

**Lưu ý hữu ích**

- Đặt @RequestMapping("/api/v1/tasks") ở class để có base path, rồi dùng @GetMapping, @PostMapping ở method.
- Ràng buộc thêm: consumes (kiểu body vào), produces (kiểu trả ra), params, headers, regex path:

```java
@GetMapping(value="/{id:\\d+}", produces="application/json")
public TaskDto get(@PathVariable long id) { ... }
```

2) @RequestParam, @PathVariable, @RequestBody

- @PathVariable lấy một phần của URL path (thường là định danh tài nguyên):

```bash
GET /tasks/42
```

```java
@GetMapping("/tasks/{id}")
TaskDto get(@PathVariable long id) { ... }
```


- @RequestParam lấy query string (lọc/sort/trang), có thể tuỳ chọn, có mặc định:

```bash
GET /tasks?status=pending&page=2&tags=a&tags=b
```

```java
@GetMapping("/tasks")
List<TaskDto> list(
   @RequestParam(required=false, defaultValue="pending") String status,
   @RequestParam(defaultValue="0") int page,
   @RequestParam List<String> tags // nhận nhiều giá trị
) { ... }
```

- @RequestBody — map HTTP request body vào một object Java

Nói với Spring: “Body của request (thường là JSON) → chuyển (deserialize) thành đối tượng này cho tôi.”

Spring dùng HttpMessageConverter (mặc định là Jackson) để chuyển JSON ⇆ object.

Ví dụ (JSON → record):

public record CreateTaskRequest(String title) {}

```java
@PostMapping(path="/tasks", consumes="application/json")
public TaskDto create(@RequestBody CreateTaskRequest body) {
    // body.title() lấy ra dữ liệu do client gửi
    return service.create(body);
}
```

**Điều kiện để @RequestBody hoạt động mượt**
* Client phải gửi header Content-Type: application/json.
Sai header ⇒ 415 Unsupported Media Type.
* JSON phải hợp lệ và khớp trường/kiểu dữ liệu ⇒ sai ⇒ 400 Bad Request.

---
@ResponseStatus — đặt HTTP status code trả về

Dùng trên method (controller handler) hoặc trên class Exception để nói “khi trả về, dùng mã này”.

Nếu không đặt, Spring dùng mặc định:

* GET thành công → 200 OK
* POST thành công → cũng thường là 200 OK (hoặc 201 nếu bạn tự đặt)
* DELETE thành công → tùy bạn (hay dùng 204 No Content)

Tại sao cần? Vì REST coi status code là một phần hợp đồng API.

```java
@PostMapping("/tasks")
@ResponseStatus(HttpStatus.CREATED) // => 201 Created
public TaskDto create(@RequestBody CreateTaskRequest body) {
    return service.create(body);
}
```

---
Theo tìm hiểu, thì so với Spring Framework, Spring Boot có một số điểm khác biệt quan trọng:
1. Cấu hình tự động (Auto Configuration): 
   - Spring Boot cung cấp tính năng cấu hình tự động, giúp giảm thiểu việc cấu hình thủ công. Nó tự động cấu hình các thành phần dựa trên các thư viện có trong classpath
     (_classpath là đường dẫn (path) mà Java Virtual Machine (JVM) sử dụng để tìm kiếm và tải các lớp (classes), thư viện (libraries, thường dưới dạng file JAR), và tài nguyên khác (như file properties, XML) khi chạy ứng dụng)._

   - Ví dụ: Nếu bạn thêm thư viện Spring MVC vào dự án, Spring Boot sẽ tự động cấu hình các thành phần cần thiết để chạy ứng dụng web.
2. Khởi tạo nhanh (Starter Dependencies):
   - Spring Boot cung cấp các "starter" dependencies, giúp bạn dễ dàng thêm các tính năng vào dự án chỉ bằng cách thêm một dependency duy nhất.
   - Ví dụ: spring-boot-starter-web để xây dựng ứng dụng web, spring-boot-starter-data-jpa để làm việc với JPA.
3. Quản lý cấu hình:
   - Spring Boot cung cấp một cách dễ dàng để quản lý cấu hình thông qua các tệp như application.properties hoặc application.yml.
4. Hỗ trợ xây dựng ứng dụng độc lập:
   - Spring Boot cho phép bạn xây dựng các ứng dụng độc lập có thể chạy mà không cần máy chủ ứng dụng bên ngoài (như Tomcat, Jetty) vì server được nhúng ngay trong file JAR build ra.


## Bean
**“Bean” là gì?**

Bean = một đối tượng (object) do Spring tạo ra, giữ hộ, và đưa cho bạn dùng.
Nó “sống” trong cái hộp gọi là ApplicationContext (container). Nhờ vậy:

* Bạn không tự new khắp nơi.
* Spring tiêm (inject) các phụ thuộc cho bạn.
* Vòng đời (tạo → dùng → huỷ) được Spring quản lý.

**Tại sao cần bean?**

* Code rời rạc (loose coupling), dễ test/thay thế.
* Không phải tự xoay xoáy chuyện khởi tạo, cấu hình, thứ tự phụ thuộc.
* Có thể cấu hình phạm vi sống (thường là singleton: một cái cho cả app).

**Làm sao “tạo” một bean?**

Có 2 con đường phổ biến:

1) Gắn annotation “stereotype” lên class → Spring quét và biến nó thành bean

```java
import org.springframework.stereotype.Service;

@Service              // -> bean tên mặc định: userService
public class UserService {
    public String hello() { return "hi"; }
}
```
2) Khai báo trong lớp cấu hình bằng @Bean
```java
import org.springframework.context.annotation.*;

@Configuration
public class AppConfig {

  @Bean               // -> bean tên: objectMapper
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
```

**Dùng bean như thế nào?**

Tiêm qua constructor (chuẩn, dễ test nhất):

```java
@RestController
public class HelloController {
    private final UserService userService;
    
    public HelloController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/hello")
   public String hello() {
        return userService.hello();
    }
}
```

**Bean khác gì object Java bình thường?**

* Object bình thường: bạn new khi cần → bạn tự chịu mọi phụ thuộc.
* Bean: Spring tạo/giữ/tiêm cho bạn → bạn chỉ khai báo “tôi cần gì”, còn việc dựng đối tượng Spring lo.

**Mini bài tập (5’)**
- Tạo @Service tên TimeService (TimeService.java) có method now() trả System.currentTimeMillis().
- Tiêm TimeService vào HealthController và trả JSON { "status":"ok", "timeMs": <now> }.

## Auto Configuration
Spring Boot có 1 cái đặc trưng goọi là Auto Configuration, nó sẽ tự động cấu hình các thành phần dựa trên các thư viện có trong classpath.
Quá trình này giúp giảm thiểu cấu hình thủ công và làm cho việc phát triển ứng dụng nhanh hơn. Nó diễn ra như sau:

Khi bạn chạy app (class @SpringBootApplication), có 3 chuyện quan trọng xảy ra:
1. @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
- EnableAutoConfiguration: nạp các lớp auto-configuration có sẵn của Boot (Web, Jackson, JPA, Security, ...) được liệt kê ở
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports (từ Spring Boot 3.x).

Ví dụ: WebMvcAutoConfiguration, JacksonAutoConfiguration, DataSourceAutoConfiguration, …

- ComponentScan: quét các lớp trong package hiện tại và các package con để tìm các bean 
được đánh dấu với @Component, @Service, @Repository, @Controller, ...
- Configuration: đánh dấu lớp hiện tại là một lớp cấu hình, có thể định nghĩa các bean.
