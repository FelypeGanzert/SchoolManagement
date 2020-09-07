insert into aluno(id, situacao, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (1, "Ativo", "Estação", "Felype", "Lapa", "07565617964", "caio@gmail.com", "Rua Carlos Pedro", "Caio de Fernando de Abreu e Souza Alburquerque", "145645", "M", "PR");
insert into aluno(id, situacao, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (2, "Aguardando", "Lacerda", "Felype", "Lapa", "123456789", "pedro@gmail.com", "Rua Carlos Pedro", "Pedro", "123456", "M", "PR");
insert into aluno(id, situacao, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (3, "Ativo", "Souza Naves", "Felype", "Lapa", "222222222", "eduarda@gmail.com", "Rua Carlos Pedro", "Eduarda", "22222", "F", "PR");
insert into aluno(id, situacao, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (4, "Inativo", "Vila do Principe", "Felype", "Lapa", "333333333", "bruno@gmail.com", "Rua Carlos Pedro", "Bruno", "3333", "M", "PR");
insert into aluno(id, situacao, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (5, "Aguardando", "Pinheirinho", "Felype", "Curitiba", "444444444", "camile@gmail.com", "Rua Carlos Pedro", "Camile", "4444", "F", "PR");
insert into responsavel(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (6, "Lugar Nenhum", "Felype", "Lapa", "223423", "paula@gmail.com", "Rua Carlos Antonio", "Paula de Fatima Souza e Filha", "424", "F", "PR");
insert into responsavel(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (7, "Los Verejos", "Felype", "Whashinton", "23423", "caska@gmail.com", "Rua Novo Pedro", "Caska das árvores maiores", "5345", "F", "PR");
insert into responsavel(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (8, "Aldeia da Folha", "Felype", "São Paulo", "53453", "camile@gmail.com", "Rua Carlos Pereiras", "Chritian de Oliveira Pereiras", "242", "M", "PR");
insert into responsavel(id, bairro, cadastrado_por, cidade, cpf, email, endereco, nome, rg, sexo, uf) values (9, "Vila Nova Fibrurgo", "Felype", "Petrolina", "444444444", "camile@gmail.com", "Rua Bermudas", "Triangulo das Bermudas", "34234", "M", "PR");
 
insert into contato(id, descricao, numero, pessoa_id) values(1,"Número do aluno","(41) 98868-0206", 1);
insert into contato(id, descricao, numero, pessoa_id) values(2,"Número da mãe", "912345678", 1);
insert into contato(id, descricao, numero, pessoa_id) values(3, "--", "9123456781234567890", 2);
insert into contato(id, descricao, numero, pessoa_id) values(4,"Namorado", "988888877", 3);
insert into contato(id, descricao, numero, pessoa_id) values(5,"Vó", "123456791", 4);
 
insert into responsavel_aluno (aluno_id, responsavel_id, parentesco) values (1, 6,  "Mãe");
insert into responsavel_aluno (aluno_id, responsavel_id, parentesco) values (3, 7, "Tia");
insert into responsavel_aluno (aluno_id, responsavel_id, parentesco) values (1, 9, "Sobinho");

insert into anotacoes (id, data, descricao, colaborador_responsavel, aluno_id) value (1, '2020-11-01 01:00:00', "uma nova anotação que será adicionada", "Felype", 1);
insert into anotacoes (id, data, descricao, colaborador_responsavel, aluno_id) value (2, '2020-10-01 02:00:00', "segunda anotação", "Felype", 3);
insert into anotacoes (id, data, descricao, colaborador_responsavel, aluno_id) value (3, '2020-09-01 03:00:00', "terceira anotação\n\n\ncom quebra de linhas", "Felype", 1);
insert into anotacoes (id, data, descricao, colaborador_responsavel, aluno_id) value (4, '2020-07-01 04:00:00', "quinta anotação", "Felype", 3);
insert into anotacoes (id, data, descricao, colaborador_responsavel, aluno_id) value (5, '2020-02-01 05:00:00', "sexta anotação", "Felype", 2);
insert into anotacoes (id, data, descricao, colaborador_responsavel, aluno_id) value (6, '2020-03-01 06:00:00', "sétima anotação com uma quantidade tremendamente um pouquinho maior do que as anotações anteriores, sendo que essa também possui quebra de linhas.\n\n\n Quebras inseridas com barras e a letra n, vamos ver se irá funcionar ou não", "Felype", 2);
    
insert into matricula(code, situacao, data_matricula, matriculado_por, motivo, responsavel_financeiro_id, aluno_id) values(1, 'Ativa', '2020-09-01', "Feh", "Teste sem resp.", null, 1);
insert into matricula(code, situacao, data_matricula, matriculado_por, motivo, responsavel_financeiro_id, aluno_id) values(2, 'Cancelada', '2010-10-01', "Feh", "Teste sem resp.", 6, 1);
insert into matricula(code, situacao, data_matricula, matriculado_por, motivo, responsavel_financeiro_id, aluno_id) values(3, 'Ativa', '2020-11-01', "Feh", "Teste com resp.", 7, 3);
 
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(1,  '2020-09-01', '2020-09-01', 5, 3, 0, "Paga", 89.90, 1);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(2,  '2020-09-02', '2020-09-02', 5, 3, 1, "Paga", 89.90, 1);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(3,  '2020-09-03', '2020-09-05', 5, 3, 2, "Aberta", 89.90, 1);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(4,  '2020-09-04', null, 5, 3, 3, "Aberta", 89.90, 1);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(5,  '2020-09-05', null, 5, 3, 4, "Aberta", 89.90, 1);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(6,  '2020-09-06', null, 5, 3, 5, "Aberta", 89.90, 1);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(7,  '2020-09-07', null, 5, 3, 6, "Aberta", 89.90, 1);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(8,  '2020-09-08', null, 5, 3, 7, "Cancelada", 89.90, 1);
 
 
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(9,  '2020-09-07', null, 5, 3, 1, "Aberta", 200.90, 2);
insert into parcela(numero_documento, data_parcela, data_pagamento, desconto, dias_desconto, parcela_numero, situacao, valor, matricula_codigo) values(10,  '2020-09-08', null, 5, 3, 2, "Aberta", 200.90, 2);   
    
insert into colaborador (id, nome, sigla, email, cargo, numero_contato, usuario_login, senha_login) values (1, "Felype Aurélio Ganzert", "Felype", "felipe.ganzert", "professor", "41988680806", "feh", "feh@123");
insert into colaborador (id, nome, sigla, email, cargo, numero_contato, usuario_login, senha_login) values (2, "Paula Cristina", "Paula", "paula@email.com", "professor", "12345", "paula", "paula123");