2024年湖南大学软件创新课做的项目
修改密码有一个BUG，成功修改时后端返回的内容和这里要匹配的内容不同,会导致程序直接闪退.
即if (data.equals("success"))这一句后端并不会返回success而是"Change password successfully"
把字符串改了即可
