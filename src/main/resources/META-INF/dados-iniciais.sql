insert into pessoa(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (1, "Estação", "Felype", "Lapa", "07565617964", "caio@gmail.com", "Rua Carlos Pedro", "Caio", "145645", "M", "PR");
insert into pessoa(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (2, "Lacerda", "Felype", "Lapa", "123456789", "pedro@gmail.com", "Rua Carlos Pedro", "Pedro", "123456", "M", "PR");
insert into pessoa(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (3, "Souza Naves", "Felype", "Lapa", "222222222", "eduarda@gmail.com", "Rua Carlos Pedro", "Eduarda", "22222", "F", "PR");
insert into pessoa(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (4, "Vila do Principe", "Felype", "Lapa", "333333333", "bruno@gmail.com", "Rua Carlos Pedro", "Bruno", "3333", "M", "PR");
insert into pessoa(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (5, "Pinheirinho", "Felype", "Curitiba", "444444444", "camile@gmail.com", "Rua Carlos Pedro", "Camile", "4444", "F", "PR");
 
insert into aluno (id, situacao) values (1, "Ativo");
insert into aluno (id, situacao) values (2, "Aguardando");
insert into aluno (id, situacao) values (3, "Ativo");
insert into aluno (id, situacao) values (5, "Ativo");
    
insert into responsavel (id) values	(2); 
insert into responsavel (id) values	(3);
insert into responsavel (id) values	(4);
    
insert into responsavel_aluno (aluno_id, responsavel_id, parentesco) values (1, 3,  "Mãe");
insert into responsavel_aluno (aluno_id, responsavel_id, parentesco) values (2, 3, "Tia");
insert into responsavel_aluno (aluno_id, responsavel_id, parentesco) values (3, 2, "Sobinho");
    
    
insert into colaborador (id, nome, sigla, email, cargo, numero_contato, usuario_login, senha_login) values (1, "Felype Aurélio Ganzert", "Felype", "felipe.ganzert", "professor", "41988680806", "feh", "feh@123");
insert into colaborador (id, nome, sigla, email, cargo, numero_contato, usuario_login, senha_login) values (2, "Paula Cristina", "Paula", "paula@email.com", "professor", "12345", "paula", "paula123");