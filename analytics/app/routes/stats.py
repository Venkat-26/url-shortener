from flask import Blueprint, jsonify, send_file, abort
import io
from app.services.analytics_service import (get_summary, get_clicks_per_day, get_device_breakdown, get_browser_breakdown, get_clicks_chart)
from app.models.db import engine
from sqlalchemy import text


stats_bp = Blueprint('stats',__name__)


@stats_bp.route('/stats/<short_code>',methods=['GET'])
def summary(short_code):
    data = get_summary(short_code)
    return jsonify(data)


@stats_bp.route('/stats/<short_code>/daily',methods=['GET'])
def daily(short_code):
    data = get_clicks_per_day(short_code)
    return jsonify(data)

@stats_bp.route('/stats/<short_code>/device', methods=['GET'])
def device(short_code):
    data = get_device_breakdown(short_code)
    return jsonify(data)

@stats_bp.route('/stats/<short_code>/browser', methods=['GET'])
def browser(short_code):
    data = get_browser_breakdown(short_code)
    return jsonify(data)

@stats_bp.route('/stats/<short_code>/chart', methods = ['GET'])
def chart(short_code):
    img_bytes = get_clicks_chart(short_code)
    return send_file(
        io.BytesIO(img_bytes),
        mimetype='image/png',
        as_attachment=False
    )

@stats_bp.route('/health',methods = ['GET'])
def health():
    try:
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
        return jsonify({"status":"ok", "database":"connected"})
    except Exception as e:
        return jsonify({"status":"error","message":str(e)}),500
