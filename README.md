# Verify Issuer
Verify Issuer нь сертификат, диплом, дансны хуулга зэрэг бичиг баримтыг блокчэйн дээр
баталгаажуулж өгөх https://github.com/corex-mn/certify-sc ухаалаг гэрээний java хэл дээрх сан юм.

- Тестнет-тэй холбогдох нөүд: `https://node-testnet.corexchain.io`
- Теснет дээрх ухаалаг гэрээний хаяг: `0xcc546a88db1af7d250a2f20dee42ec436f99e075`


- Майннет-тэй холбогдох нөүд: `https://node.corexchain.io`
- Майннет дээрх ухаалаг гэрээний хаяг: `0x5d305D8423c0f07bEaf15ba6a5264e0c88fC41B4`

## Суулгах заавар:
Доорх dependency -ийг pom.xml дотор оруулах.
```shell
<dependency>
    <groupId>io.corexchain</groupId>
    <artifactId>issuer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Классууд
## `PdfIssuer`
PDF файлын хаш утгыг тооцож ухаалаг гэрээнд бичээд, гүйлгээний мэдээлэл болон нэмэлт мэдээллүүдийг
файлын мэтадата дээр нэмж шинэ файлд хадгална.

Байгуулагчийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `smartContractAddress`                    | ухаалаг гэрээний хаяг                                 | тийм          |
| `issuerAddress`                           | илгээгч байгууллагын блокчэйний хаяг                  | тийм          |
| `issuerName`                              | илгээгч байгууллагын нэр                              | тийм          | 
| `nodeHost`                                | блокчэйн нөүдний URL                                  | тийм          |
| `chainId`                                 | блокчэйн ID                                           | үгүй          | 

`issue` функцийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `id`                                      | файлын ID  /хоосон байж болно ''/                     | үгүй          | 
| `sourceFilePath`                          | эх файлын зам                                         | тийм          |
| `destinationFilePath`                     | бүртгэсний дараа мета дата бичээд хадгалах зам        | тийм          |
| `expireDate`                              | дуусах огноо /null байж болно/                        | үгүй          |
| `desc`                                    | тайлбар                                               | тийм          |
| `additionalInfo`                          | мэтадата дээр орох нэмэлт мэдээлэл                    | хоосон байж болно ''       | 
| `privateKey`                              | хувийн түлхүүр                                        | тийм          |


Метадата дээр бичигдэх өгөгдлийн хэлбэр:
```shell
verifymn: {
    issuer: {
        name: "",
        address: ""
    },
    info: {
        name: "",
        desc: "",
        cerNum: "",
        additionalInfo: ""
    },
    version: "",
    blockchain: {
         network: "",
         smartContractAddress: ""
    }
}
```

#### Жишээ
```shell
PdfIssuer pdfIssuer = new PdfIssuer(
                "smartContractAddress",
                "issuerAddress",
                "issuerName",
                "nodeHost",
                3305
      );

String transactionID;
try {
    transactionID = pdfIssuer.issue(
            "id",
            "sourceFilePath",
            "destinationFilePath",
            "expireDate",
            "desc",
            "additionalInfo",
            "privateKey"
    );
    System.out.printf("Success: %s", transactionID);

} catch (Exception e) {
    System.out.printf("Error: %s", e);
}
```

## `verifyPdf`
Блокчэйн дээр баталгаажсан PDF файлыг шалгах

Байгуулагчийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `smartContractAddress`                    | ухаалаг гэрээний хаяг                                 | тийм          |
| `issuerAddress`                           | илгээгч байгууллагын блокчэйний хаяг                  | хоосон байж болно ''         |
| `issuerName`                              | илгээгч байгууллагын нэр                              | хоосон байж болно ''          | 
| `nodeHost`                                | блокчэйн нөүдний URL                                  | тийм          |
| `chainId`                                 | блокчэйн ID                                           | үгүй          | 

`verifyPdf` функцийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `filePath`                                | мета дататай файлын зам                               | тийм          | 


#### Жишээ

```shell
PdfIssuer pdfIssuer = new PdfIssuer(
                "smartContractAddress",
                "issuerAddress",
                "issuerName",
                "nodeHost",
                3305
      );
     
try {
    VerifyResult result = pdfIssuer.verifyPdf("filePath");

    System.out.printf("State: %s\n", result.getState());
    System.out.printf("Issuer: %s\n", result.getIssuer());
    System.out.printf("Meta-data: %s\n", result.getMetadata());
    System.out.printf("Cert: %s", result.getCert());

} catch (Exception e) {
    System.out.printf("Error: %s", e);
}
```

## `JsonIssuer`
JSON файлын хаш утгыг тооцож ухаалаг гэрээнд бичээд, гүйлгээний мэдээлэл болон нэмэлт мэдээллүүдийг
файлын мэтадата дээр нэмж шинэ файлд хадгална.

Байгуулагчийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `smartContractAddress`                    | ухаалаг гэрээний хаяг                                 | тийм          |
| `issuerAddress`                           | илгээгч байгууллагын блокчэйний хаяг                  | тийм          |
| `issuerName`                              | илгээгч байгууллагын нэр                              | тийм          | 
| `nodeHost`                                | блокчэйн нөүдний URL                                  | тийм          |
| `chainId`                                 | блокчэйн ID                                           | үгүй          | 

`issue` функцийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `id`                                      | файлын ID  /хоосон байж болно ''/      \              | үгүй          | 
| `sourceFilePath`                          | эх файлын зам                                         | тийм          |
| `destinationFilePath`                     | бүртгэсний дараа мета дата бичээд хадгалах зам        | тийм          |
| `expireDate`                              | дуусах огноо /null байж болно/                        | үгүй          |
| `desc`                                    | тайлбар                                               | тийм          |
| `additionalInfo`                          | мэтадата дээр орох нэмэлт мэдээлэл                    | тийм          | 
| `privateKey`                              | хувийн түлхүүр                                        | тийм          |


Метадата дээр бичигдэх өгөгдлийн хэлбэр:
```shell
verifymn: {
    issuer: {
        name: "",
        address: ""
    },
    info: {
        name: "",
        desc: "",
        cerNum: "",
        additionalInfo: ""
    },
    version: "",
    blockchain: {
         network: "",
         smartContractAddress: ""
    }
}
```

#### Жишээ
```shell
JsonIssuer issuer = new JsonIssuer(
        "smartContractAddress",
        "issuerAddress",
        "issuerName",
        "nodeHost",
        3305
    );

String transactionID;
try {
    transactionID =  issuer.issue(
            "id",
            "sourceFilePath",
            "destinationFilePath",
            "expireDate",
            "desc",
            "additionalInfo",
            "privateKey"
    );
    System.out.printf("Success: %s", transactionID);
} catch (Exception e) {
    System.out.printf("Error: %s", e);
}
```

## `verifyJson`
Блокчэйнд баталгаажуулсан JSON файлыг шалгах

Байгуулагчийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `smartContractAddress`                    | ухаалаг гэрээний хаяг                                 | тийм          |
| `issuerAddress`                           | илгээгч байгууллагын блокчэйний хаяг                  | хоосон байж болно ''         |
| `issuerName`                              | илгээгч байгууллагын нэр                              | хоосон байж болно ''         | 
| `nodeHost`                                | блокчэйн нөүдний URL                                  | тийм          |
| `chainId`                                 | блокчэйн ID                                           | хоосон байж болно ''         | 


`verifyJson` функцийн параметр:

| Параметр                                  | Тайлбар                                               |   Заавал эсэх |  
| -------------                             | -------------                                         | ------------- | 
| `filePath`                                | мета дататай файлын зам                               | тийм          | 


#### Жишээ
```shell
JsonIssuer issuer = new JsonIssuer(
        "smartContractAdress",
        "issuerAddress",
        "issuerName",
        "nodeHost",
        3305
    );

try {
    VerifyResult result = issuer.verifyJson("filePath");
    System.out.printf("Result: %s", result.getState());

} catch (Exception e) {
    System.out.printf("Error: %s", e);
}
```
