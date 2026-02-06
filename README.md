# 🥗 Has MS - Restoran Yönetim Sistemi

Selamlar! 👋 **Has MS**'e hoş geldiniz!

Burası, restoran işletmeciliğini keyifli ve zahmetsiz hale getirmek için tasarlanmış, **Spring Boot** tabanlı modern bir restoran yönetim sistemidir. Siparişlerden envanter takibine kadar her şeyi tek bir yerden yönetmenize olanak tanır. Restoranınızı dijital çağa taşıyın! ✨

## 🌟 Proje Hakkında

**Has MS**, restoran sahiplerinin ve çalışanlarının hayatını kolaylaştırmak için geliştirildi. Karmaşık excel tablolarından, kağıt adisyonlardan kurtulun. Hızlı, güvenilir ve kullanıcı dostu arayüzü ile restoranınızı yönetmek artık çok daha eğlenceli! 🚀

### Özellikler (Gelecek Planları & Mevcut Durum)
- 📝 **Kolay Sipariş Yönetimi:** Siparişleri anlık takip edin.
- 📦 **Stok Takibi:** Malzemeleriniz bitmeden haberiniz olsun.
- 📊 **Raporlama:** Günlük, haftalık satışlarınızı grafiklerle izleyin.
- 👨‍🍳 **Personel Yönetimi:** Ekibinizi ve vardiyalarını düzenleyin.

## 💻 Teknolojiler Kulesi

Bu harika projeyi ayağa kaldıran yapı taşları şunlar:

- **☕ Java 17:** Projenin kalbi, güçlü ve güvenilir.
- **🍃 Spring Boot 3.2.1:** Modern ve hızlı geliştirme için sihirli değneğimiz.
  - `spring-boot-starter-web`: Web dünyasına açılan kapımız.
  - `spring-boot-starter-data-jpa`: Veritabanı ile konuşan rehberimiz.
- **🐬 MySQL:** Verilerinizi güvenle saklayan hafızamız.
- **🛠️ Maven:** Projemizin mimarı, bağımlılık yöneticisi.

## 🏃‍♂️ Kurulum ve Çalıştırma Rehberi

Hadi projeyi kendi bilgisayarınızda çalıştıralım! 🛠️

### Gereksinimler
Bilgisayarınızda şunların yüklü olduğundan emin olun:
- **JDK 17** veya üzeri ☕
- **MySQL** (Veritabanı için) 🗄️
- **Maven** (Derlemek için) 🏗️

### Adım Adım Kurulum 👣

1.  **Projeyi Klonlayın** 👯
    Terminalinizi açın ve şu komutu yapıştırın:
    ```bash
    git clone https://github.com/HasCafe/Has-MS.git
    cd Has-MS
    ```

2.  **Veritabanı Ayarlarını Yapın** ⚙️
    MySQL'de `has_ms_db` adında boş bir veritabanı oluşturun.
    Ardından `src/main/resources/application.properties` dosyasını açıp kendi şifrenizi girin:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/has_ms_db
    spring.datasource.username=root
    spring.datasource.password=sizin_sifreniz
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Projeyi Derleyin** 🔨
    ```bash
    mvn clean install
    ```

4.  **Uygulamayı Başlatın** ▶️
    ```bash
    mvn spring-boot:run
    ```
    *Veya JAR dosyası ile:*
    ```bash
    java -jar target/restaurant-1.0-SNAPSHOT.jar
    ```

5.  **Tadaa! 🎉**
    Tarayıcınızı açın ve `http://localhost:8080` adresine gidin.

## 🤝 Katkıda Bulunmak İster misiniz?

Bu projeyi daha da güzelleştirmek isterseniz çok mutlu oluruz!

1.  Projeyi Fork'layın 🍴
2.  Yeni bir Branch açın (`git checkout -b feature/harika-ozellik`)
3.  Değişikliklerinizi yapın ve Commit'leyin (`git commit -m 'Harika bir özellik ekledim'`) 📝
4.  Branch'inizi Push'layın (`git push origin feature/harika-ozellik`) 🚀
5.  Bir Pull Request oluşturun 👀

---

Sevgiyle kodlandı ❤️
Has MS Ekibi
