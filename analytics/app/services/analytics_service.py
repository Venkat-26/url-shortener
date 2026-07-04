import pandas as pd
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import io
from sqlalchemy import text
from app.models.db import engine

def get_summary(short_code: str) -> dict:
    query = text("""
        SELECT
                 COUNT(*)   AS total_clicks,
                 COUNT(DISTINCT ip_address) AS unique_visitors,
                 MIN(clicked_at) AS first_click,
                 MAX(clicked_at) AS last_click
                 FROM click_events
                 WHERE short_code = :short_code
    """)

    with engine.connect() as conn:
        result = conn.execute(query, {"short_code": short_code}).fetchone()

    if result is None:
        return {"error": "No data found for the given short code." }
    
    return {
            "short_code": short_code,
            "total_clicks": result[0],
            "unique_visitors": result[1],
            "first_click": str(result[2]) if result[2] else None,
            "last_click": str(result[3]) if result[3] else None
        }



def get_clicks_per_day(short_code: str) -> list:

    query = text("""
        SELECT DATE(clicked_at) AS click_date, COUNT(*) AS click_count
        FROM click_events
        WHERE short_code = :short_code
        GROUP BY DATE(clicked_at)
        ORDER BY click_date
    """)

    with engine.connect() as conn:
        result = conn.execute(query, {"short_code": short_code}).fetchall()

    return [{"date": str(row[0]), "clicks":row[1]} for row in result]


def get_device_breakdown(short_code: str) -> list:
    query = text("""
        SELECT COALESCE(device,'unknown')   AS device, 
                 COUNT(*)   AS count,
                 FROM click_events
                 WHERE short_code = :short_code
                 GROUP BY device
                 ORDER by count DESC
    """)


    with engine.connect() as conn:
        result = conn.execute(query, {"short_code": short_code}).fetchall()

    return [{"device":row[0],"count":row[1]} for row in result]

def get_browser_breakdown(short_code: str) -> list:
    query = text("""
                 SELECT
                 COALESCE(browser, 'unknown') AS browser,
                 COUNT(*) AS count
                 FROM click_events
                 WHERE short_code = :short_code
                 GROUP BY browser
                 ORDER BY count DESC
                 """)
    
    with engine.connect() as conn: 
        result = conn.execute(query, {"short_code":short_code}).fetchall()
    
    return [{"browser":row[0],"count":row[1]} for row in result]


def get_clicks_chart(short_code: str) -> bytes:
    query = text("""
SELECT 
                 DATE(clicked_at) AS click_date,
                 COUNT(*) AS click_count
                 FROM click_events
                 WHERE short_code = :short_code
                 GROUP BY DATE(clicked_at)
                 ORDER BY click_date
                 """)
    
    with engine.connect() as conn:
        result = conn.execute(query, {"short_code": short_code}).fetchall()

    if not result:
        dates, counts = [], []
    else:
        df = pd.DataFrame(result, columns=["date","clicks"])
        dates = df["date"].astype(str).tolist()
        counts = df["clicks"].tolist()


    #Chart Specifications
    fig, ax = plt.subplots(figsize=(10,5))
    ax.plot(dates,counts, marker='o', color="#4F46E5", linewidth=2, markersize=6)
    ax.fill_between(range(len(dates)),counts,alpha=0.1, color= "#4F46E5")
    ax.set_title(f"Clicks over last 7 days  -{short_code}", fontsize=14)
    ax.set_xlabel("Date")
    ax.set_ylabel("Clicks")
    ax.set_xticks(range(len(dates)))
    ax.set_xticklabels(dates, rotation=45)
    ax.grid(True, alpha=0.3)
    plt.tight_layout()


    #Save to buffer and return as bytes
    buf = io.BytesIO()
    plt.savefig(buf,format='png', dpi=100)
    plt.close(fig)
    buf.seek(0)
    return buf.read()





    