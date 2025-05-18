import os
from flask import Flask, jsonify
from flask_cors import CORS
from marshmallow import ValidationError
from sqlalchemy import create_engine, text

from src.auth.models.database import DatabaseConfig
from src.auth.auth_exception import UserExistsException, UserNotFoundException, AccessDeniedException
from src.auth.controllers.auth import auth_blueprint
from src.secret.controllers.secret import secret_blueprint
from src.auth.controllers.test_ficheros import test_error_blueprint
from src.auth.controllers.test_db import db_errors_blueprint
from src.auth.controllers.pokemon_api import pokemon_api_blueprint


app = Flask(__name__)

# Configurar CORS
CORS(app, resources={r"/api/*": {"origins": "*"}})

# Configurar MySQL utilizando la clase DatabaseConfig
mysql = DatabaseConfig.configure_db(app)

# Mantener SQLAlchemy para los módulos que lo usan
MYSQL_URL = os.environ.get("MYSQL_URL", "mysql+pymysql://root:123456@localhost:3306/api_flask")
mysql_engine = create_engine(MYSQL_URL)

# set default version to v1
version = os.environ.get('API_VERSION', 'v1')

prefix = f"/api/{version}"


@app.errorhandler(ValidationError)
def validation_error_handler(err):
    errors = err.messages
    return jsonify(errors), 400


@app.errorhandler(UserExistsException)
def user_error_handler(e):
    return jsonify({"error": e.msg}), 400


@app.errorhandler(AccessDeniedException)
def user_error_handler(e):
    return jsonify({"error": e.msg}), 401


@app.errorhandler(UserNotFoundException)
def user_error_handler(e):
    return jsonify({"error": e.msg}), 404


app.register_blueprint(auth_blueprint, url_prefix=f'{prefix}/auth')
app.register_blueprint(secret_blueprint, url_prefix=f'{prefix}/secret')
app.register_blueprint(test_error_blueprint, url_prefix=f'{prefix}/file')
app.register_blueprint(db_errors_blueprint, url_prefix=f'{prefix}/db')
app.register_blueprint(pokemon_api_blueprint, url_prefix=f'{prefix}/pokemon')


@app.route(f'{prefix}/ping', methods=['GET'])
def ping():
    """
        Check if server is alive
        :return: "pong"
    """
    return "pong"

@app.route(f'{prefix}/db-check', methods=['GET'])
def db_check():
        # Probar conexión directa con MySQL
    try:
        cursor = mysql.connection.cursor()
        cursor.execute("SELECT username, email FROM usuarios;")
        usuarios = cursor.fetchall()
        cursor.close()

        return jsonify({
            "message": "Usuarios encontrados",
            "usuarios": usuarios
        })
    except Exception as e:
        return jsonify({
            "error": "Error de conexión a la base de datos MySQL", "details": str(e) }), 500


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')