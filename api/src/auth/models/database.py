from flask_mysqldb import MySQL
import os

# Variable global para almacenar la instancia de MySQL
mysql = None

class DatabaseConfig:
    @staticmethod
    def configure_db(app):
        """Configura la conexión a la base de datos MySQL para la aplicación Flask"""
        global mysql

        # Configuración de conexión MySQL
        app.config['MYSQL_HOST'] = os.environ.get('MYSQL_HOST', 'localhost')
        app.config['MYSQL_USER'] = os.environ.get('MYSQL_USER', 'root')
        app.config['MYSQL_PASSWORD'] = os.environ.get('MYSQL_PASSWORD', '123456')
        app.config['MYSQL_DB'] = os.environ.get('MYSQL_DB', 'api_flask')
        app.config['MYSQL_CURSORCLASS'] = 'DictCursor'  # Para que devuelva resultados como diccionarios

        # Inicializar la extensión de MySQL
        mysql = MySQL(app)

        return mysql

def get_db():
    """Devuelve la instancia global de MySQL"""
    global mysql
    return mysql