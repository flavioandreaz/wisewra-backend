-- Migration V4: Insert sample data based on Prisma seed data
-- Data from sample-courses.json and realistic user data

-- Insert sample users
INSERT INTO users (id, first_name, last_name, email, clerk_user_id, image_url) VALUES 
('cuid_user_001', 'João', 'Silva', 'joao.silva@wisevra.com', 'clerk_user_001', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150'),
('cuid_user_002', 'Maria', 'Santos', 'maria.santos@wisevra.com', 'clerk_user_002', 'https://images.unsplash.com/photo-1494790108755-2616b2b02ba7?w=150'),
('cuid_user_003', 'Pedro', 'Oliveira', 'pedro.oliveira@wisevra.com', 'clerk_user_003', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150'),
('cuid_user_004', 'Ana', 'Costa', 'ana.costa@wisevra.com', 'clerk_user_004', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150');

-- Insert courses based on sample-courses.json
INSERT INTO courses (id, status, title, slug, description, short_description, thumbnail, price, discount_price, difficulty) VALUES 
(
    'cuid_course_001', 
    'PUBLISHED',
    'Next.js para Iniciantes', 
    'nextjs-para-iniciantes',
    'Aprenda a criar aplicações modernas com Next.js, React e Tailwind CSS. Este curso abrange desde os conceitos básicos até técnicas avançadas de otimização e deployment.',
    'Aprenda a criar aplicações modernas com Next.js, React e Tailwind CSS.',
    '/sample-courses/nextjs.png',
    200.00,
    150.00,
    'EASY'
),
(
    'cuid_course_002',
    'PUBLISHED', 
    'Node.js para Backend',
    'nodejs-para-backend',
    'Domine o desenvolvimento backend com Node.js, Express e MongoDB. Aprenda a criar APIs robustas, implementar autenticação e gerenciar banco de dados.',
    'Domine o desenvolvimento backend com Node.js, Express e MongoDB.',
    '/sample-courses/node-backend.png',
    250.00,
    200.00,
    'MEDIUM'
),
(
    'cuid_course_003',
    'PUBLISHED',
    'React com TypeScript',
    'react-com-typescript',
    'Aprenda a construir aplicações React robustas utilizando TypeScript. Conheça tipagem avançada, hooks personalizados e patterns de desenvolvimento.',
    'Aprenda a construir aplicações React robustas utilizando TypeScript.',
    '/sample-courses/react-typescript.png',
    220.00,
    180.00,
    'MEDIUM'
);

-- Insert course tags (biblioteca global de tags únicas)
INSERT INTO course_tags (id, name, course_id) VALUES 
('cuid_tag_001', 'Next.js', 'cuid_course_001'),
('cuid_tag_002', 'React', 'cuid_course_001'),
('cuid_tag_003', 'Frontend', 'cuid_course_001'),
('cuid_tag_004', 'Node.js', 'cuid_course_002'),
('cuid_tag_005', 'Backend', 'cuid_course_002'),
('cuid_tag_006', 'API', 'cuid_course_002'),
('cuid_tag_007', 'TypeScript', 'cuid_course_003'),
('cuid_tag_008', 'JavaScript', 'cuid_course_003');

-- Insert course modules for Next.js course
INSERT INTO course_modules (id, title, description, course_id, module_order) VALUES 
(
    'cuid_module_001',
    'Introdução ao Next.js',
    'Introdução às funcionalidades do Next.js.',
    'cuid_course_001',
    1
),
(
    'cuid_module_002', 
    'Rotas e Navegação',
    'Entendendo como funcionam as rotas no Next.js.',
    'cuid_course_001',
    2
);

-- Insert course modules for Node.js course
INSERT INTO course_modules (id, title, description, course_id, module_order) VALUES 
(
    'cuid_module_003',
    'Configuração do Ambiente',
    'Configurando o ambiente de desenvolvimento.',
    'cuid_course_002',
    1
),
(
    'cuid_module_004', 
    'Criando uma API REST',
    'Criando uma API REST com Node.js e Express.',
    'cuid_course_002',
    2
);

-- Insert course modules for React TypeScript course
INSERT INTO course_modules (id, title, description, course_id, module_order) VALUES 
(
    'cuid_module_005',
    'Configuração do Projeto',
    'Configurando o ambiente de desenvolvimento.',
    'cuid_course_003',
    1
);

-- Insert lessons for Next.js course - Module 1
INSERT INTO course_lessons (id, title, description, video_id, duration_in_ms, lesson_order, module_id) VALUES 
(
    'cuid_lesson_001',
    'O que é Next.js?',
    'Introdução às funcionalidades do Next.js.',
    'HAXFuHDcPwM',
    600000, -- 10 minutes
    1,
    'cuid_module_001'
),
(
    'cuid_lesson_002',
    'Criando sua primeira aplicação',
    'Passo a passo para configurar e iniciar um projeto.',
    'DKS_KAmPwvs',
    900000, -- 15 minutes
    2,
    'cuid_module_001'
);

-- Insert lessons for Next.js course - Module 2
INSERT INTO course_lessons (id, title, description, video_id, duration_in_ms, lesson_order, module_id) VALUES 
(
    'cuid_lesson_003',
    'Rotas estáticas e dinâmicas',
    'Entendendo como funcionam as rotas no Next.js.',
    'HAXFuHDcPwM',
    700000, -- ~12 minutes
    1,
    'cuid_module_002'
);

-- Insert lessons for Node.js course - Module 1
INSERT INTO course_lessons (id, title, description, video_id, duration_in_ms, lesson_order, module_id) VALUES 
(
    'cuid_lesson_004',
    'Instalando Node.js',
    'Guia para instalar e configurar Node.js.',
    '89hYBEXKpqs',
    500000, -- ~8 minutes
    1,
    'cuid_module_003'
);

-- Insert lessons for Node.js course - Module 2
INSERT INTO course_lessons (id, title, description, video_id, duration_in_ms, lesson_order, module_id) VALUES 
(
    'cuid_lesson_005',
    'Rotas e Controladores',
    'Configurando endpoints e controladores.',
    'B_hKFi8mN8I',
    800000, -- ~13 minutes
    1,
    'cuid_module_004'
);

-- Insert lessons for React TypeScript course - Module 1
INSERT INTO course_lessons (id, title, description, video_id, duration_in_ms, lesson_order, module_id) VALUES 
(
    'cuid_lesson_006',
    'Criando o ambiente com Vite',
    'Configurando um novo projeto React com Vite e TypeScript.',
    'B_hKFi8mN8I',
    600000, -- 10 minutes
    1,
    'cuid_module_005'
);

-- Insert course purchases
INSERT INTO course_purchases (id, course_id, user_id) VALUES 
('cuid_purchase_001', 'cuid_course_001', 'cuid_user_002'),
('cuid_purchase_002', 'cuid_course_002', 'cuid_user_002'),
('cuid_purchase_003', 'cuid_course_001', 'cuid_user_003'),
('cuid_purchase_004', 'cuid_course_003', 'cuid_user_004');

-- Insert completed lessons (user progress)
INSERT INTO completed_lessons (id, lesson_id, course_id, user_id) VALUES 
('cuid_completed_001', 'cuid_lesson_001', 'cuid_course_001', 'cuid_user_002'),
('cuid_completed_002', 'cuid_lesson_002', 'cuid_course_001', 'cuid_user_002'),
('cuid_completed_003', 'cuid_lesson_001', 'cuid_course_001', 'cuid_user_003'),
('cuid_completed_004', 'cuid_lesson_004', 'cuid_course_002', 'cuid_user_002');

-- Insert lesson comments
INSERT INTO lesson_comments (id, lesson_id, user_id, content, parent_id) VALUES 
(
    'cuid_comment_001',
    'cuid_lesson_001',
    'cuid_user_002',
    'Excelente explicação sobre Next.js! Muito didático e fácil de entender.',
    NULL
),
(
    'cuid_comment_002',
    'cuid_lesson_001',
    'cuid_user_003',
    'Concordo! O professor explica muito bem os conceitos fundamentais.',
    'cuid_comment_001'
),
(
    'cuid_comment_003',
    'cuid_lesson_002',
    'cuid_user_002',
    'Consegui criar minha primeira aplicação seguindo o tutorial. Obrigado!',
    NULL
),
(
    'cuid_comment_004',
    'cuid_lesson_004',
    'cuid_user_002',
    'A instalação do Node.js foi bem tranquila seguindo essas instruções.',
    NULL
);

-- Insert notifications
INSERT INTO notifications (id, user_id, title, content, link) VALUES 
(
    'cuid_notif_001',
    'cuid_user_002',
    'Bem-vindo à Wisevra!',
    'Parabéns por se inscrever! Comece seus estudos agora e transforme sua carreira.',
    '/dashboard'
),
(
    'cuid_notif_002',
    'cuid_user_002', 
    'Nova aula disponível!',
    'A aula "Rotas estáticas e dinâmicas" já está disponível no curso Next.js para Iniciantes.',
    '/courses/nextjs-para-iniciantes'
),
(
    'cuid_notif_003',
    'cuid_user_003',
    'Parabéns pelo progresso!',
    'Você completou 1 aula do curso Next.js para Iniciantes. Continue assim!',
    '/courses/nextjs-para-iniciantes/progress'
),
(
    'cuid_notif_004',
    'cuid_user_004',
    'Novo curso adquirido!',
    'Você adquiriu o curso "React com TypeScript". Bons estudos!',
    '/courses/react-com-typescript'
);