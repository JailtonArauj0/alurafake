# Case Tecnico Alura

Teste técnico para uma posição de desenvolvedor Java Júnior na Alura.

## 1. Registro e Autenticação

**1.1 Realizar o registro de usuário:**

**Endpoint:** `/user/new`

**Type:** `POST`

**Header:**
```
headers: {
    "Content-Type: application/json"
}
```
**Body:**
```
{
   "name": João da Silva,
   "email": "joao@gmail.com",
   "role": "INSTRUCTOR",
   "password": "123456"
}
```

**1.2 Realizar a autenticação:**

**Endpoint:** `/user/login`

**Type:** `POST`

**Header:**
```
headers: {
    "Content-Type: application/json"
}
```
**Body:**
```
{
   "email": "joao@gmail.com"
   "password": "123456"
}
```

#### *Caso deseje utilizar os usuários gerados no DataSeeder:
```
{   
    "email": "caio@alura.com.br", // ROLE STUDENT
    "password": "123456"
} 
```
ou
```
{   
    "email": "paulo@alura.com.br", // ROLE INSTRUCTOR
    "password": "123456"
} 
```


#### **Utilize o token retornado do endpoint de autenticação para realizar as requisições.*

## 2. Criação de Atividades



**2.1 Atividade de Resposta Aberta:**

**Endpoint:** `/task/new/opentext`

**Type:** `POST`

**Header:**
```
headers: {
    "Content-Type: application/json",
    "Authorization: Bearer {token}"
}
```
**Body:**
```
{
   "courseId": 2,
   "statement": "O que aprendemos na aula de hoje?",
   "order": 1
}
```

**2.2 Atividade de alternativa única:**

**Endpoint:** `/task/new/opentext`

**Type:** `POST`

**Header:**
```
headers: {
    "Content-Type: application/json",
    "Authorization: Bearer {token}"
}
```
**Body:**
```
{
    "courseId": 42,
    "statement": "O que aprendemos hoje?",
    "order": 2,
    "options": [
        {
            "option": "Java",
            "isCorrect": true
        },
        {
            "option": "Python",
            "isCorrect": false
        },
        {
            "option": "Ruby",
            "isCorrect": false
        }
    ]
}
```

**2.3 Atividade de múltipla escolha:**

**Endpoint:** `/task/new/multiplechoice`

**Type:** `POST`

**Header:**
```
headers: {
    "Content-Type: application/json",
    "Authorization: Bearer {token}"
}
```
**Body:**
```
{
    "courseId": 42,
    "statement": "O que aprendemos hoje?",
    "order": 2,
    "options": [
        {
            "option": "Java",
            "isCorrect": true
        },
        {
            "option": "Spring",
            "isCorrect": true
        },
        {
            "option": "Ruby",
            "isCorrect": false
        }
    ]
}
```

## 3. Cursos

**3.1 Criação de Curso:**

**Endpoint:** `/course/new`

**Type:** `POST`

**Header:**
```
headers: {
    "Content-Type: application/json",
    "Authorization: Bearer {token}"
}
```
**Body:**
```
{
    "title": Curso de Java com Spring Boot,
    "description": "Curso avançado onde aprenderemos a usar o framework spring boot para criação de API's Rest."
}
```
#### **Ao criar o curso o instrutor será o usuário logado.*

**3.2 Publicação de Curso:**

**Endpoint:** `/course/{id}/publish`

**Type:** `POST`

**Header:**
```
headers: {
    "Authorization: Bearer {token}"
}
```

# Observações

- Mensagens de retorno foram padronizadas em inglês.
- Apenas usuários com Role.INSTRUCTOR conseguem criar atividades e publicar os cursos.