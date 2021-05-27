# Comprarei
**Projeto hackathon IESB 2021** - Ainda em desenvolvimento

Este projeto visa auxiliar no dia a dia dos usuários. :house:

Que seja com um simples controle financeiro, ou um lembrete do que comprar.

O app Comprarei, nasceu de uma necessidade pessoal em que houvesse uma forma simples e prática de ter listas de compras; Mas que compras? **Qualquer uma!** Que seja mercado, que seja feira, que seja materiais de construção, tudo fica a critério do usuário.

Sem a necessidade de ter que instalar um app específico para cada uma das atividades, pode-se ter todas em apenas um, poupando assim espaço e evitando trocas de tela entre apps.

O app neste primeiro momento está somente para a plataforma Android e programado em *Kotlin*.

Protótipo: [Figma](https://www.figma.com/file/yJ1XWlFengxaRsKBAKrCyf/Projeto_Mercado?node-id=0%3A1)

### Dependências :

###### Versão do _SDK_ 30

###### Versão minima do _SDK_ 27

###### Versão do java 1.8

Anko SQLite → v0.10.8 ([Github Anko](https://github.com/Kotlin/anko)).

#### Configuração

##### build.gradle(Project)

``` 
buildscript{
	ext.anko_version='0.10.8'
}
```
```
allprojects{
	maven {url 'https://jitpack.io'}
}
```
##### build.grade(:app)

```
dependecies{
  implementation "org.jetbrains.anko:anko-sqlite:$anko_version"
}
```
